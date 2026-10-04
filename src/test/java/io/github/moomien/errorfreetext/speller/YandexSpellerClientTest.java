package io.github.moomien.errorfreetext.speller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class YandexSpellerClientTest {

    private MockRestServiceServer server;
    private YandexSpellerClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://speller.test");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new YandexSpellerClient(builder.build());
    }

    @Test
    void mapsErrorsCorrectly() {
        server.expect(requestTo("http://speller.test/checkTexts"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess("""
                        [[{"code":1,"pos":0,"row":0,"col":0,"len":6,"word":"превет","s":["привет"]}],[]]
                        """, MediaType.APPLICATION_JSON));

        var result = client.checkTexts(List.of("превет", "мир"), "ru", 0);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirst().pos()).isZero();
        assertThat(result.get(0).getFirst().suggestions()).containsExactly("привет");
        assertThat(result.get(1)).isEmpty();
        server.verify();
    }

    @Test
    void throwsOnServerError() {
        server.expect(requestTo("http://speller.test/checkTexts"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> client.checkTexts(List.of("текст"), "ru", 0))
                .isInstanceOf(SpellerException.class);
    }

    @Test
    void throwsOnCountMismatch() {
        server.expect(requestTo("http://speller.test/checkTexts"))
                .andRespond(withSuccess("[[]]", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.checkTexts(List.of("а", "б"), "ru", 0))
                .isInstanceOf(SpellerException.class);
    }
}