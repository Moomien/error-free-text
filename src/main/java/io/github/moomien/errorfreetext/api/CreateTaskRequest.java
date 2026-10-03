package io.github.moomien.errorfreetext.api;

import io.github.moomien.errorfreetext.domain.Language;
import io.github.moomien.errorfreetext.validation.ContainsLetter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotNull Language language,
        @NotNull @Size(min = 3) @ContainsLetter String text){
}
