package com.phatpham.lifeos.task;

import com.phatpham.lifeos.auth.UserAccount;
import com.phatpham.lifeos.auth.AccountService;
import com.phatpham.lifeos.common.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    TaskRepository repository = mock(TaskRepository.class);
    AccountService users = mock(AccountService.class);
    TaskService service = new TaskService(repository, users);
    LocalDate date = LocalDate.of(2026, 10, 9);
    UserAccount owner = new UserAccount("alice", "hash");

    @BeforeEach
    void setup() { when(users.requireAccount("alice")).thenReturn(owner); }

    @Test
    void createsTrimmedIncompleteTask() {
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        TaskResponse result = service.create("alice", new TaskRequest("  Read  ", date));
        assertThat(result.title()).isEqualTo("Read");
        assertThat(result.completed()).isFalse();
        assertThat(result.scheduledDate()).isEqualTo(date);
    }

    @Test
    void editPreservesCompletionAndAllowsMovingToAnotherDay() {
        Task task = new Task(owner, "Read", date);
        task.setCompleted(true);
        when(repository.findByIdAndOwnerUsername(1L, "alice")).thenReturn(Optional.of(task));
        TaskResponse result = service.update("alice", 1L, new TaskRequest("Exercise", date.plusDays(1)));
        assertThat(result.completed()).isTrue();
        assertThat(result.title()).isEqualTo("Exercise");
        assertThat(result.scheduledDate()).isEqualTo(date.plusDays(1));
    }

    @Test
    void completionIsIdempotentAndReversible() {
        Task task = new Task(owner, "Read", date);
        when(repository.findByIdAndOwnerUsername(1L, "alice")).thenReturn(Optional.of(task));
        assertThat(service.complete("alice", 1L, true).completed()).isTrue();
        assertThat(service.complete("alice", 1L, true).completed()).isTrue();
        assertThat(service.complete("alice", 1L, false).completed()).isFalse();
    }

    @Test
    void cannotMutateTaskOutsideOwnerScope() {
        when(repository.findByIdAndOwnerUsername(1L, "bob")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update("bob", 1L, new TaskRequest("Other", date)))
            .isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> service.complete("bob", 1L, true)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> service.delete("bob", 1L)).isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).delete(any());
    }
}
