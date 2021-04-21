package dev.snowdrop.vertx.http.test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = { TestApplication.class, DefaultWebTestClientCodecIT.CustomWebTestClientConfiguration.class }
)
public class DefaultWebTestClientCodecIT {

    @Autowired
    private WebTestClient client;

    @Test
    public void testDefaultWebTestClient() {
        client.get()
            .exchange()
            .expectBody(CustomType.class)
            .isEqualTo(new CustomType("test"));
    }

    static class CustomType {
        private final String value;

        CustomType(String value) {
            Objects.requireNonNull(value);
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CustomType that = (CustomType) o;

            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return value;
        }
    }

    static class CustomTypeDecoder implements Decoder<CustomType> {
        @Override
        public boolean canDecode(ResolvableType elementType, MimeType mimeType) {
            return true;
        }

        @Override
        public Flux<CustomType> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, MimeType mimeType,
            Map<String, Object> hints) {
            return Flux.just(new CustomType("test"));
        }

        @Override
        public Mono<CustomType> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType,
            MimeType mimeType, Map<String, Object> hints) {
            return Mono.just(new CustomType("test"));
        }

        @Override
        public List<MimeType> getDecodableMimeTypes() {
            return Collections.singletonList(MimeType.valueOf("text/plain"));
        }
    }

    @Configuration
    static class CustomWebTestClientConfiguration {
        @Bean
        public CodecCustomizer codecCustomizer() {
            return configurer -> configurer.customCodecs().register(new CustomTypeDecoder());
        }
    }
}
