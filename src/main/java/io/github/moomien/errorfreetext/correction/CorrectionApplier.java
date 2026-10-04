package io.github.moomien.errorfreetext.correction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CorrectionApplier {
    private static final Logger log = LoggerFactory.getLogger(CorrectionApplier.class);

    public String apply(String text, List<SpellError> errors) {
        Objects.requireNonNull(text, "text must not be null");
        Objects.requireNonNull(errors, "errors must not be null");

        List<SpellError> fromEnd = errors.stream()
                .sorted(Comparator.comparingInt(SpellError::pos).reversed())
                .toList();

        StringBuilder result = new StringBuilder(text);
        int boundary = text.length();

        for (SpellError error : fromEnd) {
            if (error.suggestions().isEmpty()){
                continue;
            }
            int end = error.pos() + error.len();
            if (error.pos() < 0 || error.len() < 0  || error.pos() + error.len() > text.length()) {
                throw new IllegalArgumentException("Error position out of text boundary " + error);
            }
            if (end > boundary) {
                log.warn("Skipping overlapping error: {}", error);
                continue;
            }
            result.replace(error.pos(), end, error.suggestions().getFirst());
            boundary = error.pos();
        }
        return result.toString();
    }
}
