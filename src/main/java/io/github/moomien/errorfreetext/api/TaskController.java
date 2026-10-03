package io.github.moomien.errorfreetext.api;


import io.github.moomien.errorfreetext.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<CreateTaskResponse> create(
            @Valid
            @RequestBody
            CreateTaskRequest request) {
     UUID id = taskService.create(request.text(), request.language());
     return ResponseEntity.status(HttpStatus.CREATED).body(new CreateTaskResponse(id));
    }

    @GetMapping("/{id}")
    public TaskResponse get(@PathVariable UUID id) {
        return TaskResponse.from(taskService.getById(id));
    }
}
