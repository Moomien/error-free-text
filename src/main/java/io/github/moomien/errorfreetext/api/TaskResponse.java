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
        return new TaskResponse(
                task.getStatus(),
                task.getCorrectedText(),
                task.getErrorMessage());
    }
}