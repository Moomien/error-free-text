package io.github.moomien.errorfreetext.speller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class SpellerClientConfig {

    @Bean
    RestClient spellerRestClient(RestClient.Builder builder, SpellerProperties props) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(props.connectTimeout());
        factory.setReadTimeout(props.readTimeout());
        return builder
                .baseUrl(props.baseUrl())
                .requestFactory(factory)
                .build();
    }
}
