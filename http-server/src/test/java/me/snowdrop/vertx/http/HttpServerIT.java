package me.snowdrop.vertx.http;

import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.XmlPath.CompatibilityMode.HTML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.web.reactive.function.server.RouterFunctions.resources;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {
        "server.port=" + HttpServerIT.PORT,
        "server.compression.enabled=true"
    }
)
public class HttpServerIT {

    static final int PORT = 8080;

    @Before
    public void setUp() {
        RestAssured.port = PORT;
    }

    @Test
    public void shouldGetResponseWithContent() {
        given()
            .body("test")
            .get("echo")
            .then()
            .assertThat()
            .statusCode(is(equalTo(200)))
            .body(is(equalTo("test")));
    }

    @Test
    public void shouldGetResponseWithoutContent() {
        given()
            .get("noop")
            .then()
            .assertThat()
            .statusCode(is(equalTo(204)));
    }

    @Test
    public void shouldGetStaticContent() {
        String html = given()
            .get("static/index.html")
            .andReturn()
            .asString();

        XmlPath xmlPath = new XmlPath(HTML, html);
        assertThat(xmlPath.getString("html.body.div")).isEqualTo("Test div");
    }

    @Test
    public void shouldUpdateCookie() {
        given()
            .cookie("counter", "10")
            .get("cookie-counter")
            .then()
            .assertThat()
            .cookie("counter", equalTo("11"));
    }

    @Test
    public void shouldUpdateHeader() {
        given()
            .header("counter", "10")
            .get("header-counter")
            .then()
            .assertThat()
            .header("counter", equalTo("11"));
    }

    @Test
    public void shouldGetCompressedBody() {
        given()
            .header("Accept-Encoding", "gzip")
            .and()
            .body("test")
            .get("echo")
            .then()
            .assertThat()
            .body(is(equalTo("test")))
            .and()
            .header("Content-Encoding", "gzip");
    }

    @TestConfiguration
    static class Routers {

        @Bean
        public RouterFunction<ServerResponse> testRouter() {
            return route()
                .GET("/echo", request -> ok().body(request.bodyToMono(String.class), String.class))
                .GET("/noop", request -> noContent().build())
                .GET("/cookie-counter", request -> {
                    int counter = request.cookies()
                        .get("counter")
                        .stream()
                        .map(HttpCookie::getValue)
                        .map(Integer::valueOf)
                        .findAny()
                        .orElse(0);

                    ResponseCookie cookie = ResponseCookie.from("counter", String.valueOf(counter + 1)).build();
                    return noContent().cookie(cookie).build();
                })
                .GET("/header-counter", request -> {
                    int counter = request.headers()
                        .header("counter")
                        .stream()
                        .map(Integer::valueOf)
                        .findAny()
                        .orElse(0);

                    return noContent().header("counter", String.valueOf(counter + 1)).build();
                })
                .build();
        }

        @Bean
        public RouterFunction<ServerResponse> staticRouter() {
            return resources("/**", new ClassPathResource("static"));
        }
    }
}
