package me.snowdrop.vertx.http;

import io.restassured.path.xml.XmlPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.XmlPath.CompatibilityMode.HTML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpServerIT {

    @Value("${local.server.port}")
    private int port;

    @Test
    public void shouldGetResponseWithContent() {
        given()
            .port(port)
            .and()
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
            .port(port)
            .get("noop")
            .then()
            .assertThat()
            .statusCode(is(equalTo(204)));
    }

    @Test
    public void shouldGetStaticContent() {
        String html = given()
            .port(port)
            .get("static/index.html")
            .andReturn()
            .asString();

        XmlPath xmlPath = new XmlPath(HTML, html);
        assertThat(xmlPath.getString("html.body.div")).isEqualTo("Test div");
    }

    @Test
    public void shouldUpdateCookie() {
        given()
            .port(port)
            .cookie("counter", "10")
            .get("cookie-counter")
            .then()
            .assertThat()
            .cookie("counter", equalTo("11"));
    }

    @Test
    public void shouldUpdateHeader() {
        given()
            .port(port)
            .header("counter", "10")
            .get("header-counter")
            .then()
            .assertThat()
            .header("counter", equalTo("11"));
    }
}
