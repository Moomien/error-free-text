package io.github.moomien.errorfreetext.scheduler;

import io.github.moomien.errorfreetext.correction.CorrectionApplier;
import io.github.moomien.errorfreetext.correction.SpellError;
import io.github.moomien.errorfreetext.correction.SpellOptionsResolver;
import io.github.moomien.errorfreetext.correction.TextSplitter;
import io.github.moomien.errorfreetext.domain.Language;
import io.github.moomien.errorfreetext.domain.Task;
import io.github.moomien.errorfreetext.domain.TaskRepository;
import io.github.moomien.errorfreetext.speller.SpellerClient;
import io.github.moomien.errorfreetext.speller.SpellerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskProcessorTest {

    private static final UUID TASK_ID = UUID.randomUUID();
    private static final String TEXT = "Helo wrld";

    @Mock private TaskRepository repository;
    @Mock private SpellerClient spellerClient;
    @Mock private TextSplitter textSplitter;
    @Mock private SpellOptionsResolver optionsResolver;
    @Mock private CorrectionApplier correctionApplier;
    @Mock private Task task;

    @InjectMocks
    private TaskProcessor processor;

    @BeforeEach
    void setUp() {
        when(repository.findById(TASK_ID)).thenReturn(Optional.of(task));
        when(task.getLanguage()).thenReturn(Language.EN);
        when(task.getText()).thenReturn(TEXT);
        when(textSplitter.split(TEXT)).thenReturn(List.of("Helo ", "wrld"));
    }

    @Test
    void correctsEachFragmentAndJoinsResult() {
        List<SpellError> firstErrors = List.of();
        List<SpellError> secondErrors = List.of();
        when(optionsResolver.resolve(anyString())).thenReturn(0);
        when(spellerClient.checkTexts(List.of("Helo "), "en", 0)).thenReturn(List.of(firstErrors));
        when(spellerClient.checkTexts(List.of("wrld"), "en", 0)).thenReturn(List.of(secondErrors));
        when(correctionApplier.apply("Helo ", firstErrors)).thenReturn("Hello ");
        when(correctionApplier.apply("wrld", secondErrors)).thenReturn("world");

        processor.process(TASK_ID);

        verify(task).markDone("Hello world");
        verify(task, never()).markFailed(anyString());
        verify(repository).save(task);
    }

    @Test
    void spellerErrorMarksTaskFailedWithoutPartialText() {
        List<SpellError> firstErrors = List.of();
        when(optionsResolver.resolve(anyString())).thenReturn(0);
        when(spellerClient.checkTexts(List.of("Helo "), "en", 0)).thenReturn(List.of(firstErrors));
        when(correctionApplier.apply("Helo ", firstErrors)).thenReturn("Hello ");
        when(spellerClient.checkTexts(List.of("wrld"), "en", 0))
                .thenThrow(new SpellerException("Speller unavailable"));

        processor.process(TASK_ID);

        verify(task).markFailed("Speller unavailable");
        verify(task, never()).markDone(anyString());
        verify(repository).save(task);
    }

    @Test
    void unexpectedErrorMarksTaskFailed() {
        when(optionsResolver.resolve(anyString())).thenThrow(new IllegalStateException("boom"));

        processor.process(TASK_ID);

        verify(task).markFailed(anyString());
        verify(task, never()).markDone(anyString());
        verify(repository).save(task);
    }
}