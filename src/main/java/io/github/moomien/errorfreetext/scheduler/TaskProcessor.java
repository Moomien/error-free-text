package io.github.moomien.errorfreetext.scheduler;

import io.github.moomien.errorfreetext.correction.CorrectionApplier;
import io.github.moomien.errorfreetext.correction.SpellError;
import io.github.moomien.errorfreetext.correction.SpellOptionsResolver;
import io.github.moomien.errorfreetext.correction.TextSplitter;
import io.github.moomien.errorfreetext.domain.Task;
import io.github.moomien.errorfreetext.domain.TaskRepository;
import io.github.moomien.errorfreetext.speller.SpellerClient;
import io.github.moomien.errorfreetext.speller.SpellerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class TaskProcessor {
    private static final Logger log = LoggerFactory.getLogger(TaskProcessor.class);

    private static final String INTERNAL_ERROR_MESSAGE = "Internal processing error";

    private final TaskRepository repository;
    private final SpellerClient spellerClient;
    private final TextSplitter textSplitter;
    private final SpellOptionsResolver optionsResolver;
    private final CorrectionApplier correctionApplier;

    public TaskProcessor(
            TaskRepository repository,
            SpellerClient spellerClient,
            TextSplitter textSplitter,
            SpellOptionsResolver optionsResolver,
            CorrectionApplier correctionApplier) {
        this.repository = repository;
        this.spellerClient = spellerClient;
        this.textSplitter = textSplitter;
        this.optionsResolver = optionsResolver;
        this.correctionApplier = correctionApplier;
    }

    public void process(UUID taskId) {
        Task task = repository.findById(taskId).orElseThrow();

        try {
            String lang = task.getLanguage().name().toLowerCase(Locale.ROOT);

            String corrected = correct(task.getText(), lang);
            task.markDone(corrected);
            log.info("Task {} corrected", taskId);
        } catch (SpellerException e) {
            task.markFailed(e.getMessage());
            log.warn("Task {} failed: {}", taskId, e.getMessage());
        } catch (RuntimeException e) {
            task.markFailed(INTERNAL_ERROR_MESSAGE);
            log.error("Task {} failed unexpectedly", taskId, e);
        }
        repository.save(task);
    }

    private String correct(String text, String lang) {
        StringBuilder result = new StringBuilder(text.length());

        for (String fragment : textSplitter.split(text)){
            int options = optionsResolver.resolve(fragment);

            List<SpellError> errors = spellerClient
                    .checkTexts(List.of(fragment), lang, options)
                    .get(0);
            result.append(correctionApplier.apply(fragment, errors));
        }
        return result.toString();
    }
}
