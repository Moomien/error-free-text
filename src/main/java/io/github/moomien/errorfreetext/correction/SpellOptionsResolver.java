package io.github.moomien.errorfreetext.correction;

import java.util.Objects;
import java.util.regex.Pattern;

public class SpellOptionsResolver {
    private static final Pattern URL_PATTERN = Pattern.compile("(?i)(?:https?://|www\\.)\\S+");

    public int resolve(String text) {
        Objects.requireNonNull(text, "text must not be null");

        int options = 0;
        if (containsDigit(text)) {
            options |= SpellOption.IGNORE_DIGITS.mask();
        }
        if (URL_PATTERN.matcher(text).find()) {
            options |= SpellOption.IGNORE_URLS.mask();
        }

        return options;
    };

    private boolean containsDigit(String text) {
        return text.codePoints().anyMatch(Character::isDigit);
    }
}
