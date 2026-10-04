package io.github.moomien.errorfreetext.speller;

public class SpellerException extends RuntimeException {
    public SpellerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpellerException(String message) {
        super(message);
    }
}
