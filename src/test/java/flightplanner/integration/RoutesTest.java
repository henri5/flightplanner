package flightplanner.integration;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertEquals;
import static org.springframework.http.HttpStatus.OK;

import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContentAssert;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoutesTest {

  static {
    System.setProperty("spring.datasource.url", "jdbc:tc:postgresql:9.6.5://localhost/test");
    System.setProperty("spring.datasource.driver-class-name", "org.testcontainers.jdbc.ContainerDatabaseDriver");
  }

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private TestRestTemplate restTemplate;

  @Before
  public void before() {
    jdbcTemplate.update("DELETE FROM flight");
  }

  @Test
  public void canFindDirectFlight() {
    insertFlight("A", "B", 1000);
    insertFlight("A", "B", 2000);

    String response = get("/api/routes/A/B");

    assertJsonMap(response)
        .contains(
            entry("source", "A"),
            entry("destination", "B"),
            entry("distance", 1000));
    assertJsonList(response, "$.flights")
        .extracting("source", "destination", "distance")
        .containsExactly(
            tuple("A", "B", 1000));
  }

  @Test
  public void canFindRouteWithSingleStop() {
    insertFlight("A", "B", 1000);
    insertFlight("A", "C", 3000);
    insertFlight("B", "C", 1000);

    String response = get("/api/routes/A/C");

    assertJsonMap(response)
        .contains(
            entry("source", "A"),
            entry("destination", "C"),
            entry("distance", 2000));
    assertJsonList(response, "$.flights")
        .extracting("source", "destination", "distance")
        .containsExactly(
            tuple("A", "B", 1000),
            tuple("B", "C", 1000));
  }

  @Test
  public void canFindRouteWithThreeStops() {
    insertFlight("A", "B", 1000);
    insertFlight("B", "C", 1000);
    insertFlight("C", "D", 1000);
    insertFlight("D", "E", 1000);

    String response = get("/api/routes/A/E");

    assertJsonMap(response)
        .contains(
            entry("source", "A"),
            entry("destination", "E"),
            entry("distance", 4000));
    assertJsonList(response, "$.flights")
        .extracting("source", "destination", "distance")
        .containsExactly(
            tuple("A", "B", 1000),
            tuple("B", "C", 1000),
            tuple("C", "D", 1000),
            tuple("D", "E", 1000));
  }

  @Test
  public void canFindShorterRouteWithMoreSteps() {
    insertFlight("A", "B1", 1000);
    insertFlight("A", "B2", 1000);
    insertFlight("B1", "C", 1000);
    insertFlight("B2", "D", 3000);
    insertFlight("C", "D", 1000);
    insertFlight("D", "E", 1000);

    String response = get("/api/routes/A/E");

    assertJsonMap(response)
        .contains(
            entry("source", "A"),
            entry("destination", "E"),
            entry("distance", 4000));
    assertJsonList(response, "$.flights")
        .extracting("source", "destination", "distance")
        .containsExactly(
            tuple("A", "B1", 1000),
            tuple("B1", "C", 1000),
            tuple("C", "D", 1000),
            tuple("D", "E", 1000));
  }

  @Test
  public void failsToFindRouteWithFourStops() {
    insertFlight("A", "B", 1000);
    insertFlight("B", "C", 1000);
    insertFlight("C", "D", 1000);
    insertFlight("D", "E", 1000);
    insertFlight("E", "F", 1000);

    String response = get("/api/routes/A/F");

    assertJsonMap(response)
        .containsExactly(
            entry("message", "Route not found"));
  }

  private MapAssert<Object, Object> assertJsonMap(String json) {
    return new JsonContentAssert(String.class, json).extractingJsonPathMapValue("$");
  }

  private ListAssert<Object> assertJsonList(String json, String expression) {
    return new JsonContentAssert(String.class, json).extractingJsonPathArrayValue(expression);
  }

  private String get(String path) {
    ResponseEntity<String> response = makeRequest(path);
    assertEquals(OK, response.getStatusCode());
    return response.getBody();
  }

  private ResponseEntity<String> makeRequest(String path) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<>(headers), String.class);
  }

  private void insertFlight(String source, String destination, long distance) {
    jdbcTemplate.update("INSERT INTO flight(source, destination, distance) VALUES (?, ?, ?)", source, destination, distance);
  }
}
