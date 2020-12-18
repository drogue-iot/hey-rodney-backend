package io.drogue.iot.rodney;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;

@QuarkusTest
public class BackendTest {

    @Test
    void simple1() {

        var json = new JsonObject();

        given()
                .header("Ce-specversion", "1.0")
                .header("Ce-id", "1234")
                .header("Ce-time", "2020-01-01T01:01Z")
                .header("Ce-type", "type")
                .header("Ce-source", "text")
                .header("Ce-device_id", "foo")
                .header("Ce-model_id", "bar")
                .header("Content-Type", "application/json")
                .body(json.toString())
                .when().post("/")
                .then()
                .statusCode(202);
    }

}
