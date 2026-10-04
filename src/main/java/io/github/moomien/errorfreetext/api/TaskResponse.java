package io.github.moomien.errorfreetext.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.moomien.errorfreetext.domain.Task;
import io.github.moomien.errorfreetext.domain.TaskStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskResponse(
        TaskStatus status,
        String correctedText,
        String errorMessage){
    public static TaskResponse from(Task task) {
        return switch (task.getStatus()) {
            case DONE -> new TaskResponse(task.getStatus(), task.getCorrectedText(), null);
            case ERROR -> new TaskResponse(task.getStatus(), null, task.getErrorMessage());
            case NEW, IN_PROGRESS -> new TaskResponse(task.getStatus(), null, null);
        };
    }
}