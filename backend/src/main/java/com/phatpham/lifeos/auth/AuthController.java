package com.phatpham.lifeos.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserAccountRepository users;
    private final PasswordEncoder passwords;

    public AuthController(UserAccountRepository users, PasswordEncoder passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    @GetMapping("/csrf")
    public CsrfResponse csrf(CsrfToken token) { return new CsrfResponse(token.getHeaderName(), token.getToken()); }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse register(@Valid @RequestBody RegisterRequest request) {
        if (request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được vượt quá 72 byte UTF-8.");
        }
        try {
            UserAccount user = users.saveAndFlush(new UserAccount(request.username(), passwords.encode(request.password())));
            return new AccountResponse(user.getUsername());
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tên đăng nhập đã được sử dụng.");
        }
    }

    @GetMapping("/me")
    public AccountResponse me(Principal principal) { return new AccountResponse(principal.getName()); }

    public record RegisterRequest(
        @NotBlank @Pattern(regexp = "[a-z0-9_]{3,40}", message = "Dùng 3–40 ký tự thường, số hoặc dấu gạch dưới.") String username,
        @NotBlank @Size(min = 8, max = 72) String password
    ) {
        @Override
        public String toString() { return "RegisterRequest[username=" + username + ", password=[REDACTED]]"; }
    }
    public record AccountResponse(String username) {}
    public record CsrfResponse(String headerName, String token) {}
}
