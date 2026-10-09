package com.phatpham.lifeos.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class AccountService {
    private final UserAccountRepository users;

    public AccountService(UserAccountRepository users) {
        this.users = users;
    }

    public UserAccount requireAccount(String username) {
        return users.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Vui lòng đăng nhập lại."));
    }
}
