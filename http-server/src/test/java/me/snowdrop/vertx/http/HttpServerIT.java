package me.snowdrop.vertx.http;

import io.restassured.RestAssured;
import io.restassured.path.xml.XmlPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.XmlPath.CompatibilityMode.HTML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

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
}
