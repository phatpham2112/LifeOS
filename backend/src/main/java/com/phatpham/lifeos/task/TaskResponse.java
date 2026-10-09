package com.phatpham.lifeos.task;

import java.time.LocalDate;

public record TaskResponse(Long id, String title, LocalDate scheduledDate, boolean completed) {
    static TaskResponse from(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getScheduledDate(), task.isCompleted());
    }
}
