package io.github.moomien.errorfreetext.scheduler;

import io.github.moomien.errorfreetext.domain.Task;
import io.github.moomien.errorfreetext.domain.TaskRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
public class TaskClaimer {
    private final TaskRepository repository;

    public TaskClaimer(TaskRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<UUID> claimNewTasks(int limit) {
        List<Task> tasks = repository.findNewForUpdate(limit);
        tasks.forEach(Task::markInProgress);

        return tasks.stream()
                .map(Task::getId)
                .toList();
    }
}
