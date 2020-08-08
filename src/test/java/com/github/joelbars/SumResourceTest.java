package com.github.joelbars;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class SumResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/sum")
          .then()
             .statusCode(200)
             .body(is("Hello!"));
    }

}