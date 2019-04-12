package me.snowdrop.vertx.http.it;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public abstract class BaseFileTransferIT {

    private static final Path ORIGINAL_FILE = Paths.get("target/original-file");

    private static final Path EXPECTED_FILE = Paths.get("target/expected-file");

    protected abstract WebClient getClient();

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(ORIGINAL_FILE);
        Files.deleteIfExists(EXPECTED_FILE);
        createTestFile(ORIGINAL_FILE, getFileSize());
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(ORIGINAL_FILE);
        Files.deleteIfExists(EXPECTED_FILE);
    }

    @Test
    public void shouldUploadLargeFile() throws IOException {
        HttpStatus status = getClient()
            .post()
            .uri("/upload")
            .header("path", EXPECTED_FILE.toString())
            .body(fromMultipartData("file", new UrlResource(ORIGINAL_FILE.toUri())))
            .exchange()
            .map(ClientResponse::statusCode)
            .block(Duration.ofMinutes(5));

        assertThat(status).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(Files.size(EXPECTED_FILE)).isEqualTo(Files.size(ORIGINAL_FILE));
    }

    @Test
    public void shouldDownloadLargeFile() throws IOException {
        Flux<DataBuffer> input = getClient()
            .get()
            .uri("/download")
            .header("path", ORIGINAL_FILE.toAbsolutePath().toString())
            .retrieve()
            .bodyToFlux(DataBuffer.class);

        writeToFile(input, EXPECTED_FILE).blockLast(Duration.ofMinutes(5));

        assertThat(Files.size(EXPECTED_FILE)).isEqualTo(Files.size(ORIGINAL_FILE));
    }

    protected int getFileSize() {
        return Integer.MAX_VALUE;
    }

    private void createTestFile(Path path, int fileSize) throws IOException {
        Random random = new Random();
        RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw");
        file.setLength(fileSize);

        int stepSize = 1024 * 1024 * 10;
        int counter = 0;
        while (counter < fileSize) {
            if (fileSize - counter < stepSize) {
                stepSize = fileSize - counter; // Step is too big for the leftover bytes
            }
            byte[] bytes = new byte[stepSize];
            random.nextBytes(bytes);
            file.write(bytes);
            counter += stepSize;
        }

        file.close();
    }

    private static Flux<DataBuffer> writeToFile(Flux<DataBuffer> input, Path path) {
        FileOutputStream output;
        try {
            output = new FileOutputStream(path.toFile());
        } catch (FileNotFoundException e) {
            return Flux.error(e);
        }

        return DataBufferUtils.write(input, output)
            .doOnTerminate(() -> {
                try {
                    output.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
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

            return ok().body(BodyInserters.fromResource(new FileSystemResource(path)));
        }

        private Mono<ServerResponse> uploadHandler(ServerRequest request) {
            String path = request.headers()
                .header("path")
                .get(0);
            Flux<DataBuffer> input = request.bodyToFlux(Part.class)
                .flatMap(Part::content);

            return writeToFile(input, Paths.get(path))
                .then(noContent().build());
        }
    }
}
