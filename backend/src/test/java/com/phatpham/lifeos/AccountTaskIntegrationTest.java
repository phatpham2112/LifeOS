package com.phatpham.lifeos;

import com.phatpham.lifeos.auth.UserAccountRepository;
import com.phatpham.lifeos.task.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AccountTaskIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired UserAccountRepository users;
    @Autowired TaskRepository tasks;
    @Autowired ObjectMapper json;

    @BeforeEach
    void clean() { tasks.deleteAll(); users.deleteAll(); }

    void register(String name) throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"" + name + "\",\"password\":\"password123\"}"))
            .andExpect(status().isCreated());
    }

    MockHttpSession login(String name) throws Exception {
        return (MockHttpSession) mvc.perform(post("/api/auth/login").with(csrf())
            .param("username", name).param("password", "password123"))
            .andExpect(status().isNoContent()).andReturn().getRequest().getSession(false);
    }

    @Test
    void registerLoginAndLogoutWithRealCsrfToken() throws Exception {
        var csrfResult = mvc.perform(get("/api/auth/csrf")).andExpect(status().isOk()).andReturn();
        MockHttpSession session = (MockHttpSession) csrfResult.getRequest().getSession(false);
        JsonNode token = json.readTree(csrfResult.getResponse().getContentAsString());
        mvc.perform(post("/api/auth/register").session(session)
            .header(token.get("headerName").asText(), token.get("token").asText())
            .contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"alice\",\"password\":\"password123\"}"))
            .andExpect(status().isCreated());
        assertThat(users.findByUsername("alice").orElseThrow().getPasswordHash()).startsWith("$2").isNotEqualTo("password123");
        String oldId = session.getId();
        mvc.perform(post("/api/auth/login").session(session)
            .header(token.get("headerName").asText(), token.get("token").asText())
            .param("username", "alice").param("password", "password123"))
            .andExpect(status().isNoContent());
        assertThat(session.getId()).isNotEqualTo(oldId);
        mvc.perform(get("/api/auth/me").session(session)).andExpect(jsonPath("$.username").value("alice"));
        mvc.perform(post("/api/tasks").session(session).header(token.get("headerName").asText(), token.get("token").asText())
            .contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Read\",\"scheduledDate\":\"2026-10-09\"}"))
            .andExpect(status().isForbidden());
        var refreshed = mvc.perform(get("/api/auth/csrf").session(session)).andReturn();
        JsonNode newToken = json.readTree(refreshed.getResponse().getContentAsString());
        mvc.perform(post("/api/auth/logout").session(session)
            .header(newToken.get("headerName").asText(), newToken.get("token").asText())).andExpect(status().isNoContent());
        assertThat(session.isInvalid()).isTrue();
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsInvalidAccountsCredentialsAndMissingCsrf() throws Exception {
        mvc.perform(get("/api/tasks?date=2026-10-09")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"A\",\"password\":\"short\"}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.username").exists());
        register("alice");
        mvc.perform(post("/api/auth/register").with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"alice\",\"password\":\"password123\"}"))
            .andExpect(status().isConflict());
        mvc.perform(post("/api/auth/login").with(csrf()).param("username", "alice").param("password", "wrong"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void taskLifecycleAndIsolationBetweenAccounts() throws Exception {
        register("alice"); register("bob");
        MockHttpSession alice = login("alice"), bob = login("bob");
        var created = mvc.perform(post("/api/tasks").session(alice).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\" Read \",\"scheduledDate\":\"2026-10-09\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("Read"))
            .andExpect(jsonPath("$.completed").value(false)).andReturn();
        long id = json.readTree(created.getResponse().getContentAsString()).get("id").asLong();
        String path = "/api/tasks/" + id;
        mvc.perform(get("/api/tasks?date=2026-10-09").session(alice)).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/tasks?date=2026-10-10").session(alice)).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/tasks?date=2026-10-09").session(bob)).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(put(path).session(bob).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\"Stolen\",\"scheduledDate\":\"2026-10-10\"}")).andExpect(status().isNotFound());
        mvc.perform(patch(path + "/completion").session(bob).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"completed\":true}")).andExpect(status().isNotFound());
        mvc.perform(delete(path).session(bob).with(csrf())).andExpect(status().isNotFound());
        mvc.perform(patch(path + "/completion").session(alice).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"completed\":true}")).andExpect(jsonPath("$.completed").value(true));
        mvc.perform(put(path).session(alice).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{\"title\":\"Exercise\",\"scheduledDate\":\"2026-10-10\"}"))
            .andExpect(jsonPath("$.completed").value(true));
        mvc.perform(get("/api/tasks?date=2026-10-09").session(alice)).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/tasks?date=2026-10-10").session(alice)).andExpect(jsonPath("$[0].title").value("Exercise"));
        mvc.perform(delete(path).session(alice).with(csrf())).andExpect(status().isNoContent());
        mvc.perform(delete(path).session(alice).with(csrf())).andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidTaskInput() throws Exception {
        register("alice"); MockHttpSession session = login("alice");
        for (String body : new String[] {
            "{\"title\":\"  \",\"scheduledDate\":\"2026-10-09\"}",
            "{\"title\":\"Read\"}",
            "{\"title\":\"Read\",\"scheduledDate\":\"not-a-date\"}",
            "{\"title\":\"" + "a".repeat(201) + "\",\"scheduledDate\":\"2026-10-09\"}"
        }) {
            mvc.perform(post("/api/tasks").session(session).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
        }
        mvc.perform(get("/api/tasks?date=invalid").session(session)).andExpect(status().isBadRequest());
        mvc.perform(patch("/api/tasks/1/completion").session(session).with(csrf()).contentType(MediaType.APPLICATION_JSON)
            .content("{}")).andExpect(status().isBadRequest());
        assertThat(tasks.count()).isZero();
    }
}
