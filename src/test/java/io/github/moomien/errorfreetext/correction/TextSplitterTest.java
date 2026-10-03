package io.github.moomien.errorfreetext.correction;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextSplitterTest {
    @Test
    void shortTextIsSingleChunk() {
        assertThat(new TextSplitter(100).split("привет мир"))
                .containsExactly("привет мир");
    }

    @Test
    void emptyTextGivesNoChunks() {
        assertThat(new TextSplitter(10).split("")).isEmpty();
    }

    @Test
    void doesNotSplitWords() {
        assertThat(new TextSplitter(8).split("один два три"))
                .containsExactly("один ", "два три");
    }

    @Test
    void validSplitWithWhitespace() {
        String text = "Первая  строка\nвторая\r\n\tтретья строка";
        List<String> chunks = new TextSplitter(7).split(text);

        assertThat(String.join("", chunks)).isEqualTo(text);
        assertThat(chunks).allSatisfy(c -> assertThat(c.length()).isLessThanOrEqualTo(7));
    }

    @Test
    void hardCutsOversizedWord() {
        assertThat(new TextSplitter(4).split("abcdefghij"))
                .containsExactly("abcd", "efgh", "ij");
    }

    @Test
    void doesNotSplitSurrogatePair() {
        String text = "ab\uD83D\uDE00cd"; // ab😀cd
        List<String> chunks = new TextSplitter(3).split(text);

        assertThat(String.join("", chunks)).isEqualTo(text);
        assertThat(chunks).noneMatch(c -> Character.isHighSurrogate(c.charAt(c.length() - 1)));
    }

    @Test
    void rejectsTooSmallLimit() {
        assertThatThrownBy(() -> new TextSplitter(1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
