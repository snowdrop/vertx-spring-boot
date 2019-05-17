package dev.snowdrop.vertx.http.it;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.BodyInserters.fromDataBuffers;
import static org.springframework.web.reactive.function.BodyInserters.fromResource;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public class HttpFileTransferIT extends TestBase {

    private static final Path ORIGINAL_FILE = Paths.get("target/original-file").toAbsolutePath();

    private static final Path EXPECTED_FILE = Paths.get("target/expected-file").toAbsolutePath();

    private static final long FILE_SIZE = Integer.MAX_VALUE;

    @BeforeClass
    public static void setUpClass() throws IOException {
        Files.deleteIfExists(ORIGINAL_FILE);
        createTestFile();
    }

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(EXPECTED_FILE);
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(EXPECTED_FILE);
        stopServer();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        Files.deleteIfExists(ORIGINAL_FILE);
    }

    @Test
    public void shouldUploadLargeFile() throws IOException {
        startServerWithoutSecurity(UploadRouter.class);

        Vertx vertx = getBean(Vertx.class);
        Flux<DataBuffer> dataBuffers = readFile(vertx, ORIGINAL_FILE);

        HttpStatus status = getWebClient()
            .post()
            .body(fromDataBuffers(dataBuffers))
            .exchange()
            .map(ClientResponse::statusCode)
            .block(Duration.ofMinutes(5));

        assertThat(status).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(Files.size(EXPECTED_FILE)).isEqualTo(Files.size(ORIGINAL_FILE));
    }

    @Test
    public void shouldDownloadLargeFile() throws IOException {
        startServerWithoutSecurity(DownloadRouter.class);

        Flux<DataBuffer> buffers = getWebClient()
            .get()
            .retrieve()
            .bodyToFlux(DataBuffer.class);

        writeToFile(buffers, EXPECTED_FILE)
            .block(Duration.ofMinutes(5));

        assertThat(Files.size(EXPECTED_FILE)).isEqualTo(Files.size(ORIGINAL_FILE));
    }

    private static void createTestFile() throws IOException {
        RandomAccessFile file = new RandomAccessFile(ORIGINAL_FILE.toFile(), "rw");
        file.setLength(FILE_SIZE);
        file.close();
    }

    private static Flux<DataBuffer> readFile(Vertx vertx, Path path) {
        AsyncFile file = vertx.fileSystem().openBlocking(path.toString(), new OpenOptions());

        Flux<Buffer> buffers = Flux.create(sink -> {
            file.pause();
            file.endHandler(v -> sink.complete());
            file.exceptionHandler(sink::error);
            file.handler(sink::next);
            sink.onRequest(file::fetch);
        });

        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

        return Flux.from(buffers)
            .map(Buffer::getBytes)
            .map(dataBufferFactory::wrap);
    }

    private static Mono<Void> writeToFile(Flux<DataBuffer> input, Path path) {
        try {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(path, CREATE_NEW, WRITE);
            return DataBufferUtils.write(input, channel).then();
        } catch (IOException e) {
            return Mono.error(e);
        }
    }

    @Configuration
    static class DownloadRouter {
        @Bean
        public RouterFunction<ServerResponse> downloadRouter() {
            return route()
                .GET("/", request -> ok().body(fromResource(new FileSystemResource(ORIGINAL_FILE))))
                .build();
        }
    }

    @Configuration
    static class UploadRouter {
        @Bean
        public RouterFunction<ServerResponse> uploadRouter() {
            return route()
                .POST("/", request -> {
                    Flux<DataBuffer> buffers = request.bodyToFlux(DataBuffer.class);

                    return writeToFile(buffers, EXPECTED_FILE)
                        .then(noContent().build());
                })
                .build();
        }
    }
}
