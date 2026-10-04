package io.github.moomien.errorfreetext.speller;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "speller")
public record SpellerProperties(String baseUrl, Duration connectTimeout, Duration readTimeout) {
}
