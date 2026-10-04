package io.github.moomien.errorfreetext.service;

import io.github.moomien.errorfreetext.domain.Language;
import io.github.moomien.errorfreetext.domain.Task;
import io.github.moomien.errorfreetext.domain.TaskRepository;
import io.github.moomien.errorfreetext.domain.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Test
    void createSavesNewTask() {
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));
        TaskService service = new TaskService(taskRepository);

        service.create("Helo world", Language.EN);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(TaskStatus.NEW);
        assertThat(saved.getText()).isEqualTo("Helo world");
        assertThat(saved.getLanguage()).isEqualTo(Language.EN);
    }

    @Test
    void getByIdThrowsIfMissing () {
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        TaskService service = new TaskService(taskRepository);

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}