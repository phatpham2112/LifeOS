package com.phatpham.lifeos.task;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }

    @GetMapping
    public List<TaskResponse> list(Principal principal, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.list(principal.getName(), date);
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(Principal principal, @Valid @RequestBody TaskRequest request) {
        TaskResponse task = service.create(principal.getName(), request);
        return ResponseEntity.created(URI.create("/api/tasks/" + task.id())).body(task);
    }

    @PutMapping("/{id}")
    public TaskResponse update(Principal principal, @PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return service.update(principal.getName(), id, request);
    }

    @PatchMapping("/{id}/completion")
    public TaskResponse complete(Principal principal, @PathVariable Long id, @Valid @RequestBody CompletionRequest request) {
        return service.complete(principal.getName(), id, request.completed());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Principal principal, @PathVariable Long id) {
        service.delete(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    public record CompletionRequest(@NotNull Boolean completed) {}
}
