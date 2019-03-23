package edu.boardgames.collections.explorer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class PlayInfoResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/playinfo")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

}