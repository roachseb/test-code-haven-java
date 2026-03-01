package app;

import static io.restassured.RestAssured.given;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.Arc;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.mutiny.ext.auth.User;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class PartyRessourceTest {

  @BeforeEach
  void setup() {
    // create 3 users for testing
    given()
        .body(new Party())
        .header("Content-Type", "application/json")
        .when()
        .post("/api/parties");

    given()
        .body(new Party(List.of()))
        .header("Content-Type", "application/json")
        .when()
        .post("/api/parties");

    given()
        .body(new Party(List.of("Cholo")))
        .header("Content-Type", "application/json")
        .when()
        .post("/api/parties");
  }

  @AfterEach
  @Disabled("Skipping this test")
  void tearDown() {
    // delete all users
    given().when().delete("/api/parties");
  }

  @Test
  @Disabled("Skipping this test")
  void testGetAllUsers() {
    given().when().get("/api/parties").then().statusCode(206);
  }

  @Test
  @Disabled("Skipping this test")
  void testGetUserById() throws Exception {
    // Jsonyfy the user
    Party testUser = new Party();
    ObjectMapper objectMapper = Arc.container().instance(ObjectMapper.class).get();
    String jsonbody = objectMapper.writeValueAsString(testUser);

    // create a user
    Party user =
        given()
            .body(jsonbody)
            .header("Content-Type", "application/json")
            .when()
            .post("/api/parties")
            .then()
            .statusCode(201)
            .extract()
            .as(Party.class);

    Log.info("User created: " + user.id.toString());
    Log.info("calling path: /api/users/" + user.id.toString());

    var res = given().when().get("/api/party/" + user.id.toString()).then().extract().response();

    Log.info("Response: " + res.getStatusCode());
    Log.info("Response: " + res.getBody().asString());
  }
}
