package com.phatpham.lifeos.auth;

import com.phatpham.lifeos.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_accounts")
public class UserAccount extends BaseEntity {
    @Column(nullable = false, unique = true, length = 40)
    private String username;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    protected UserAccount() {}
    public UserAccount(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}
