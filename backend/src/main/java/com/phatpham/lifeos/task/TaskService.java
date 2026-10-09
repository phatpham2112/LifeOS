package com.phatpham.lifeos.task;

import com.phatpham.lifeos.auth.AccountService;
import com.phatpham.lifeos.common.exception.ResourceNotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository repository;

    private final AccountService users;

    public TaskService(TaskRepository repository, AccountService users) {
        this.repository = repository;
        this.users = users;
    }

    public List<TaskResponse> list(String username, LocalDate date) {
        return repository.findByOwnerUsernameAndScheduledDateOrderByCreatedAtAscIdAsc(username, date).stream().map(TaskResponse::from).toList();
    }

    @Transactional
    public TaskResponse create(String username, TaskRequest request) {
        return TaskResponse.from(repository.save(new Task(users.requireAccount(username), request.title(), request.scheduledDate())));
    }

    @Transactional
    public TaskResponse update(String username, Long id, TaskRequest request) {
        Task task = find(username, id);
        task.update(request.title(), request.scheduledDate());
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse complete(String username, Long id, boolean completed) {
        Task task = find(username, id);
        task.setCompleted(completed);
        return TaskResponse.from(task);
    }

    @Transactional
    public void delete(String username, Long id) { repository.delete(find(username, id)); }

    private Task find(String username, Long id) {
        return repository.findByIdAndOwnerUsername(id, username).orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }
}
