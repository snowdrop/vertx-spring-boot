package me.snowdrop.vertx.http.it;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import io.vertx.core.Vertx;
import me.snowdrop.vertx.http.client.VertxClientHttpConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.Part;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Category(SlowTests.class)
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "server.port=" + Ports.FILE_TRANSFER_IT,
        "logging.level.me.snowdrop=INFO"
    }
)
public class FileTransferIT {

    private static final String BASE_URL = String.format("http://localhost:%d", Ports.FILE_TRANSFER_IT);

    private static final Path ORIGINAL_LARGE_FILE = Paths.get("target/original-large-file");

    private static final Path EXPECTED_LARGE_FILE = Paths.get("target/expected-large-file");

    @Autowired
    private Vertx vertx;

    private WebClient client;

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(ORIGINAL_LARGE_FILE);
        Files.deleteIfExists(EXPECTED_LARGE_FILE);

        client = WebClient.builder()
            .clientConnector(new VertxClientHttpConnector(vertx))
            .baseUrl(BASE_URL)
            .build();
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(ORIGINAL_LARGE_FILE);
        Files.deleteIfExists(EXPECTED_LARGE_FILE);
    }

    @Test
    public void shouldUploadLargeFile() throws IOException {
        int fileSize = Integer.MAX_VALUE;
        createTestFile(ORIGINAL_LARGE_FILE, fileSize);

        Mono<HttpStatus> responseMono = client.post()
            .uri("/upload")
            .header("path", EXPECTED_LARGE_FILE.toString())
            .body(fromMultipartData("file", new UrlResource(ORIGINAL_LARGE_FILE.toUri())))
            .exchange()
            .map(ClientResponse::statusCode);

        StepVerifier.create(responseMono)
            .expectNext(HttpStatus.NO_CONTENT)
            .verifyComplete();
        assertThat(Files.size(EXPECTED_LARGE_FILE)).isEqualTo(fileSize);
    }

    @Test
    public void shouldDownloadLargeFile() throws IOException {
        int fileSize = Integer.MAX_VALUE;
        createTestFile(ORIGINAL_LARGE_FILE, fileSize);

        Flux<DataBuffer> input = client.get()
            .uri("/download")
            .header("path", ORIGINAL_LARGE_FILE.toAbsolutePath().toString())
            .retrieve()
            .bodyToFlux(DataBuffer.class);

        AsynchronousFileChannel output = AsynchronousFileChannel.open(EXPECTED_LARGE_FILE, CREATE_NEW, WRITE);

        Mono<Void> completionMono = DataBufferUtils.write(input, output)
            .then();

        StepVerifier.create(completionMono)
            .verifyComplete();

        assertThat(Files.size(EXPECTED_LARGE_FILE)).isEqualTo(fileSize);
    }

    private void createTestFile(Path path, int size) throws IOException {
        Random random = new Random();
        FileChannel channel = FileChannel.open(path, CREATE_NEW, WRITE);

        int counter = 0;
        while (counter < size) {
            int nextAmount = 1024 * 1024;
            if (counter + nextAmount >= size || counter + nextAmount < 0) {
                nextAmount = size - counter;
            }
            byte[] bytes = new byte[nextAmount];
            random.nextBytes(bytes);
            counter += channel.write(ByteBuffer.wrap(bytes));
        }

        channel.close();
    }

    @TestConfiguration
    static class Routers {

        @Bean
        public RouterFunction<ServerResponse> testRouter() {
            return route()
                .GET("/download", this::downloadHandler)
                .POST("/upload", this::uploadHandler)
                .build();
        }

        private Mono<ServerResponse> downloadHandler(ServerRequest request) {
            String path = request.headers()
                .header("path")
                .get(0);
            URL url;
            try {
                url = URI.create("file://" + path).toURL();
            } catch (MalformedURLException e) {
                return Mono.error(e);
            }

            return ok().body(BodyInserters.fromResource(new FileUrlResource(url)));
        }

        private Mono<ServerResponse> uploadHandler(ServerRequest request) {
            Flux<DataBuffer> input = request.bodyToFlux(Part.class)
                .flatMap(Part::content);
            AsynchronousFileChannel output;

            try {
                String path = request.headers()
                    .header("path")
                    .get(0);
                output = AsynchronousFileChannel.open(Paths.get(path), CREATE_NEW, WRITE);
            } catch (IOException e) {
                return Mono.error(e);
            }

            return DataBufferUtils.write(input, output)
                .then(noContent().build());
        }
    }
}
