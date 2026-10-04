package io.github.moomien.errorfreetext.correction;

import java.util.List;

public record SpellError(int pos, int len, List<String> suggestions) {
    public SpellError{
        suggestions = List.copyOf(suggestions);
    }
}
