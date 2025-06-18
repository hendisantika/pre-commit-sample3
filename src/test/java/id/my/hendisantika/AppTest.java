package id.my.hendisantika;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

/**
 * Created by IntelliJ IDEA. Project : pre-commit-sample10 User: hendisantika Link:
 * s.id/hendisantika Email: hendisantika@yahoo.co.id Telegram : @hendisantika34 Date: 10/02/25 Time:
 * 13.13 To change this template use File | Settings | File Templates.
 */
public class AppTest {
  @BeforeAll
  static void setup() {
    System.out.println("@BeforeAll - executes once before all test methods in this class");
  }

  @BeforeEach
  void init() {
    System.out.println("@BeforeEach - executes before each test method in this class");
  }

  @DisplayName("Single test successful")
  @org.junit.jupiter.api.Test
  void testSingleSuccessTest() {
    System.out.println("Success");
  }

  @org.junit.jupiter.api.Test
  @Disabled("Not implemented yet")
  void testShowSomething() {
    System.out.println("testShowSomething");
  }

  @org.junit.jupiter.api.Test
  void groupAssertions() {
    int[] numbers = {0, 1, 2, 3, 4};
    assertAll(
        "numbers",
        () -> assertEquals(numbers[1], 1),
        () -> assertEquals(numbers[3], 3),
        () -> assertEquals(numbers[2], 2),
        () -> assertEquals(numbers[4], 4));
  }

  @org.junit.jupiter.api.Test
  void shouldThrowException() {
    Throwable exception =
        assertThrows(
            UnsupportedOperationException.class,
            () -> {
              throw new UnsupportedOperationException("Not supported");
            });
    assertEquals("Not supported", exception.getMessage());
  }

  @org.junit.jupiter.api.Test
  void assertThrowsException() {
    String str = null;
    assertThrows(
        IllegalArgumentException.class,
        () -> {
          Integer.valueOf(str);
        });
  }
}
