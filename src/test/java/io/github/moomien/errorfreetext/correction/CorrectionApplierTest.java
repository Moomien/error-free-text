package io.github.moomien.errorfreetext.correction;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class CorrectionApplierTest {
    private final CorrectionApplier applier = new CorrectionApplier();

    @Test
    void noErrorsReturnsSameText() {
        assertThat(applier.apply("всё верно", List.of())).isEqualTo("всё верно");
    }

    @Test
    void replaceSingleError() {
        var errors = List.of(new SpellError(0, 6, List.of("привет")));
        assertThat(applier.apply("превет мир", errors)).isEqualTo("привет мир");
    }

    @Test
    void replacementKeepsPositions() {
        var errors = List.of(
                new SpellError(2, 3, List.of("меня")),
                new SpellError(6, 5, List.of("кошка")));
        assertThat(applier.apply("у мну кашка", errors)).isEqualTo("у меня кошка");
    }

    @Test
    void appliesUnsortedErrors() {
        var errors = List.of(
                new SpellError(0, 6, List.of("привет")),
                new SpellError(7, 3, List.of("мир")));
        assertThat(applier.apply("превет мип", errors)).isEqualTo("привет мир");
    }

    @Test
    void emptySuggestionsAreSkipped() {
        var errors = List.of(new SpellError(0, 5, List.of()));
        assertThat(applier.apply("абвгд тест", errors)).isEqualTo("абвгд тест");
    }

    @Test
    void firstSuggestionIsUsed() {
        var errors = List.of(new SpellError(0, 3, List.of("кот", "код")));
        assertThat(applier.apply("кто", errors)).isEqualTo("кот");
    }

    @Test
    void preservesWhitespace() {
        var errors = List.of(new SpellError(4, 3, List.of("мир")));
        assertThat(applier.apply("  \n мип\t\n", errors)).isEqualTo("  \n мир\t\n");
    }

    @Test
    void emojiSurrogatePairIndices() {
        // 😀 занимает 2 char, поэтому "превет" начинается с pos = 3
        var errors = List.of(new SpellError(3, 6, List.of("привет")));
        assertThat(applier.apply("😀 превет", errors)).isEqualTo("😀 привет");
    }

    @Test
    void overlappingErrorIsSkipped() {
        var errors = List.of(
                new SpellError(0, 6, List.of("привет")),
                new SpellError(3, 5, List.of("XXX")));
        assertThat(applier.apply("превет мир", errors)).isEqualTo("преXXXир");
    }

    @Test
    void outOfBoundsPositionThrows() {
        var errors = List.of(new SpellError(5, 10, List.of("x")));
        assertThatThrownBy(() -> applier.apply("короткий", errors))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullTextThrows() {
        assertThatThrownBy(() -> applier.apply(null, List.of()))
                .isInstanceOf(NullPointerException.class);
    }
}
