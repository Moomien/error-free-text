package io.github.moomien.errorfreetext.scheduler;

import io.github.moomien.errorfreetext.domain.TaskRepository;
import io.github.moomien.errorfreetext.domain.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

@Component
public class StuckTaskRecovery implements SmartInitializingSingleton {

    private static final Logger log = LoggerFactory.getLogger(StuckTaskRecovery.class);

    private final TaskRepository taskRepository;

    public StuckTaskRecovery(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void afterSingletonsInstantiated() {
        int recovered = taskRepository.updateStatus(TaskStatus.IN_PROGRESS, TaskStatus.NEW);
        if (recovered > 0) {
            log.warn("Recovered {} tasks stuck in IN_PROGRESS", recovered);
        } else {
            log.info("No stuck tasks found");
        }
    }
}