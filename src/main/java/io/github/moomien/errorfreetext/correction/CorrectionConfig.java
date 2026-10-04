package io.github.moomien.errorfreetext.correction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorrectionConfig {
    private static final int YANDEX_MAX_CHUNK_LENGTH = 10_000;

    @Bean
    public TextSplitter textSplitter() {
        return new TextSplitter(YANDEX_MAX_CHUNK_LENGTH);
    }

    @Bean
    public SpellOptionsResolver spellOptionsResolver() {
        return new SpellOptionsResolver();
    }

    @Bean
    public CorrectionApplier correctionApplier() {
        return new CorrectionApplier();
    }
}