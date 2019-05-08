package me.snowdrop.vertx.http.it;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public class HttpFileTransferIT extends TestBase {

    private static final Path ORIGINAL_FILE = Paths.get("target/original-file");

    private static final Path EXPECTED_FILE = Paths.get("target/expected-file");

    private static final int FILE_SIZE = Integer.MAX_VALUE;

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(ORIGINAL_FILE);
        Files.deleteIfExists(EXPECTED_FILE);
        createTestFile();
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(ORIGINAL_FILE);
        Files.deleteIfExists(EXPECTED_FILE);
        stopServer();
    }

    @Test
    public void shouldUploadLargeFile() throws IOException {
        startServerWithoutSecurity(UploadRouter.class);

        HttpStatus status = getWebClient()
            .post()
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
        startServerWithoutSecurity(DownloadRouter.class);

        Flux<DataBuffer> input = getWebClient()
            .get()
            .header("path", ORIGINAL_FILE.toAbsolutePath().toString())
            .retrieve()
            .bodyToFlux(DataBuffer.class);

        writeToFile(input, EXPECTED_FILE).blockLast(Duration.ofMinutes(5));

        assertThat(Files.size(EXPECTED_FILE)).isEqualTo(Files.size(ORIGINAL_FILE));
    }

    private void createTestFile() throws IOException {
        Random random = new Random();
        RandomAccessFile file = new RandomAccessFile(ORIGINAL_FILE.toFile(), "rw");
        file.setLength(FILE_SIZE);

        int stepSize = 1024 * 1024 * 10;
        int counter = 0;
        while (counter < FILE_SIZE) {
            if (FILE_SIZE - counter < stepSize) {
                stepSize = FILE_SIZE - counter; // Step is too big for the leftover bytes
            }
            byte[] bytes = new byte[stepSize];
            random.nextBytes(bytes);
            file.write(bytes);
            counter += stepSize;
        }

        file.close();
    }

    private static Flux<DataBuffer> writeToFile(Flux<DataBuffer> input, Path path) {
        AsynchronousFileChannel output;
        try {
            output = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        } catch (IOException e) {
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

    @Configuration
    static class DownloadRouter {
        @Bean
        public RouterFunction<ServerResponse> downloadRouter() {
            return route()
                .GET("/", request -> {
                    String path = request.headers()
                        .header("path")
                        .get(0);

                    return ok().body(BodyInserters.fromResource(new FileSystemResource(path)));
                })
                .build();
        }
    }

    @Configuration
    static class UploadRouter {
        @Bean
        public RouterFunction<ServerResponse> uploadRouter() {
            return route()
                .POST("/", accept(MediaType.MULTIPART_FORM_DATA), request -> {
                    String path = request.headers()
                        .header("path")
                        .get(0);
                    Flux<DataBuffer> input = request.bodyToFlux(Part.class)
                        .flatMap(Part::content);

                    return writeToFile(input, Paths.get(path))
                        .then(noContent().build());
                })
                .build();
        }
    }
}
