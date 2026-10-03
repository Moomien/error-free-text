package io.github.moomien.errorfreetext.service;

import io.github.moomien.errorfreetext.domain.Language;
import io.github.moomien.errorfreetext.domain.Task;
import io.github.moomien.errorfreetext.domain.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;

    //autowired maybe
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public UUID create(String text, Language language) {
        Task task = taskRepository.save(Task.createNew(text, language));
        log.info("Task created: id={}, language={}, length={}",
                task.getId(), language, text.length());

        return task.getId();
    }

    @Transactional(readOnly = true)
    public Task getById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
