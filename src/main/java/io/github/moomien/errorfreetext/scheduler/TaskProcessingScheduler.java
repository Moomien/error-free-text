package io.github.moomien.errorfreetext.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class TaskProcessingScheduler {
    private static final Logger log = LoggerFactory.getLogger(TaskProcessingScheduler.class);


    private final TaskClaimer claimer;
    private final TaskProcessor processor;
    private final int batchSize;

    public TaskProcessingScheduler(
            TaskClaimer claimer,
            TaskProcessor processor,
            @Value("${app.scheduler.batch-size}") int batchSize){
        this.claimer = claimer;
        this.processor = processor;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.fixed-delay-ms}")
    public void processNewTasks() {
        List<UUID> ids = claimer.claimNewTasks(batchSize);

        if (ids.isEmpty()) {
            return;
        }
        log.info("Processing {} tasks", ids.size());

        ids.forEach(this::processSafely);
    }

    private void processSafely(UUID taskId) {
        try {
            processor.process(taskId);
        } catch (RuntimeException e) {
            log.error("Task {} processing aborted", taskId, e);
        }
    }
}
