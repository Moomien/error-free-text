package io.github.moomien.errorfreetext.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(schema = "error_free_text", name = "tasks")
public class Task {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length =  2)
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    @Column(name = "corrected_text")
    private String correctedText;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Task() {
    }

    public static Task createNew(String text, Language language) {
        Task task = new Task();

        task.id = UUID.randomUUID();
        task.text = text;
        task.language = language;
        task.status = TaskStatus.NEW;
        LocalDateTime now = LocalDateTime.now();
        task.createdAt = now;
        task.updatedAt = now;

        return task;
    }

    public void markInProgress() {
        status = TaskStatus.IN_PROGRESS;
        updatedAt = LocalDateTime.now();
    }

    public void markDone(String correctedText) {
        status = TaskStatus.DONE;
        this.correctedText = correctedText;
        updatedAt = LocalDateTime.now();
    }

    public void markFailed(String message) {
        status = TaskStatus.ERROR;
        errorMessage = message;
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Language getLanguage() {
        return language;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getCorrectedText() {
        return correctedText;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
