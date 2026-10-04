package io.github.moomien.errorfreetext.api;

import io.github.moomien.errorfreetext.domain.Task;
import io.github.moomien.errorfreetext.domain.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskResponseTest {

    @Test
    void doneReturnsCorrectedTextOnly() {
        Task task = mock(Task.class);
        when(task.getStatus()).thenReturn(TaskStatus.DONE);
        when(task.getCorrectedText()).thenReturn("Hello world");

        TaskResponse response = TaskResponse.from(task);

        assertThat(response).isEqualTo(new TaskResponse(TaskStatus.DONE, "Hello world", null));
    }

    @Test
    void errorReturnsErrorMessageOnly() {
        Task task = mock(Task.class);
        when(task.getStatus()).thenReturn(TaskStatus.ERROR);
        when(task.getErrorMessage()).thenReturn("Speller unavailable");

        TaskResponse response = TaskResponse.from(task);

        assertThat(response).isEqualTo(new TaskResponse(TaskStatus.ERROR, null, "Speller unavailable"));
    }

    @ParameterizedTest
    @EnumSource(value = TaskStatus.class, names = {"NEW", "IN_PROGRESS"})
    void unfinishedReturnsStatusOnly(TaskStatus status) {
        Task task = mock(Task.class);
        when(task.getStatus()).thenReturn(status);

        TaskResponse response = TaskResponse.from(task);

        assertThat(response).isEqualTo(new TaskResponse(status, null, null));
    }
}