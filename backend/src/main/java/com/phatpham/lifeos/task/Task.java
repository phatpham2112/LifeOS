package com.phatpham.lifeos.task;

import com.phatpham.lifeos.common.persistence.BaseEntity;
import com.phatpham.lifeos.auth.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, updatable = false)
    private UserAccount owner;

    @Column(nullable = false, length = 200)
    private String title;
    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;
    @Column(nullable = false)
    private boolean completed;

    protected Task() {}

    public Task(UserAccount owner, String title, LocalDate scheduledDate) {
        this.owner = owner;
        update(title, scheduledDate);
    }

    public void update(String title, LocalDate scheduledDate) {
        this.title = title.strip();
        this.scheduledDate = scheduledDate;
    }

    public void setCompleted(boolean completed) { this.completed = completed; }
    public String getTitle() { return title; }
    public LocalDate getScheduledDate() { return scheduledDate; }
    public boolean isCompleted() { return completed; }
}
