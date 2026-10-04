package io.github.moomien.errorfreetext.speller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
record YandexSpellError(int code, int pos, int len, String word, List<String> s) {
}
