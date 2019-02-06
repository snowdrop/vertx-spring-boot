package me.snowdrop.vertx.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HttpServerIT {

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

}
