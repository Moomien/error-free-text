package io.github.moomien.errorfreetext.speller;

import io.github.moomien.errorfreetext.correction.SpellError;
import java.util.List;

public interface SpellerClient {
    List<List<SpellError>> checkTexts(List<String> texts, String lang, int options);
}
