package io.github.moomien.errorfreetext.service;

import java.util.UUID;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(UUID id) {
        super("Task with id: %s not found".formatted(id));
    }
}
