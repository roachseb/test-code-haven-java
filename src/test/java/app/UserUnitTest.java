package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserUnitTest {

  @Test
  void testUser() {
    Party user = new Party(List.of("John", "Doe"));
    assertNotNull(user.id);
    assertEquals(2, user.roles.size());
  }
}
