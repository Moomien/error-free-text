package io.github.moomien.errorfreetext.correction;

public enum SpellOption {
    IGNORE_DIGITS(2),
    IGNORE_URLS(4);

    private final int mask;

    SpellOption(int mask) {
        this.mask = mask;
    }

    public int mask() {
        return mask;
    }
}
