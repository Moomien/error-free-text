package io.github.moomien.errorfreetext.speller;

import io.github.moomien.errorfreetext.correction.SpellError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Component
public class YandexSpellerClient implements SpellerClient {
    private static final Logger log = LoggerFactory.getLogger(YandexSpellerClient.class);

    private static final ParameterizedTypeReference<List<List<YandexSpellError>>>
            RESPONSE_TYPE = new ParameterizedTypeReference<>() { };

    private final RestClient restClient;

    public YandexSpellerClient(RestClient client) {
        this.restClient = client;
    }


    @Override
    public List<List<SpellError>> checkTexts(List<String> texts, String lang, int options) {
        var form = new LinkedMultiValueMap<String, String>();
        texts.forEach(text -> form.add("text", text));
        form.add("lang", lang);
        form.add("options", String.valueOf(options));

        List<List<YandexSpellError>> response;
        try {
            response = restClient.post()
                    .uri("/checkTexts")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(RESPONSE_TYPE);
        } catch (RestClientException e) {
            log.error("Yandex Speller request failed: {}", e.getMessage());
            throw new SpellerException("Yandex Speller request failed: " + e.getMessage(), e);
        }

        if(response == null || response.size() != texts.size()) {
            throw new SpellerException("Unexpected Yandex Speller response: expected " + texts.size() +
                    " results, got " + (response == null ? "null" : response.size()));
        }
        log.debug("Yandex Speller checked {} fragments", texts.size());
        return response.stream()
                .map(errors -> errors.stream().map(YandexSpellerClient::toSpellError).toList())
                .toList();
    }

    private static SpellError toSpellError(YandexSpellError e) {
        return new SpellError(e.post(), e.len(), e.s() == null ? List.of() : e.s());
    }
}
