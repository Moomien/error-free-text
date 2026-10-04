package io.github.moomien.errorfreetext.correction;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SpellOptionsResolverTest {
    private final SpellOptionsResolver resolver = new SpellOptionsResolver();

    @Test
    void plainTextGivesNoOptions() {
        assertThat(resolver.resolve("привет мир")).isZero();
    }

    @Test
    void digitsEnableIgnoreDigits() {
        assertThat(resolver.resolve("у меня 3 кота")).isEqualTo(2);
    }

    @Test
    void urlEnablesIgnoreUrls() {
        assertThat(resolver.resolve("смотри https://example.com тут")).isEqualTo(4);
        assertThat(resolver.resolve("смотри https://example.com")).isEqualTo(4);
    }

    @Test
    void digitsAndUrlEnableBoth() {
        assertThat(resolver.resolve("2 ссылки http://a.ru")).isEqualTo(6);
    }

    @Test
    void digitsInsideUrlIsDigits() {
        assertThat(resolver.resolve("http://site1.ru")).isEqualTo(6);
    }

    @Test
    void nullTextThrows() {
        assertThatThrownBy(() -> resolver.resolve(null))
                .isInstanceOf(NullPointerException.class);
    }
}
