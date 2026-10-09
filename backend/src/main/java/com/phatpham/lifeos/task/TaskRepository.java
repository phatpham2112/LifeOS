package com.phatpham.lifeos.task;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwnerUsernameAndScheduledDateOrderByCreatedAtAscIdAsc(String username, LocalDate scheduledDate);
    Optional<Task> findByIdAndOwnerUsername(Long id, String username);
}
