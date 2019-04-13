package flightplanner.integration;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.Test;

import flightplanner.integration.support.DatabaseTestMixin;
import flightplanner.integration.support.IntegrationTest;

public class RoutesTest extends IntegrationTest {

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
    assertJsonList(response, "$.connections")
        .extracting("source", "destination", "distance", "type")
        .containsExactly(
            tuple("A", "B", 1000, "AIR"));
  }

  @Test
  public void failsToFindRouteToSelf() {
    insertFlight("A", "B", 1000);
    insertFlight("B", "A", 2000);

    String response = get("/api/routes/A/A");

    assertJsonMap(response)
        .containsExactly(
            entry("message", "Route not found"));
  }

  @Test
  public void failsToFindRouteToSelfWithoutAnyFlights() {
    String response = get("/api/routes/A/A");

    assertJsonMap(response)
        .containsExactly(
            entry("message", "Route not found"));
  }

  @Test
  public void failsToFindNonExistingRoute() {
    String response = get("/api/routes/A/B");

    assertJsonMap(response)
        .containsExactly(
            entry("message", "Route not found"));
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
    assertJsonList(response, "$.connections")
        .extracting("source", "destination", "distance", "type")
        .containsExactly(
            tuple("A", "B", 1000, "AIR"),
            tuple("B", "C", 1000, "AIR"));
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
    assertJsonList(response, "$.connections")
        .extracting("source", "destination", "distance", "type")
        .containsExactly(
            tuple("A", "B", 1000, "AIR"),
            tuple("B", "C", 1000, "AIR"),
            tuple("C", "D", 1000, "AIR"),
            tuple("D", "E", 1000, "AIR"));
  }

  @Test
  public void canFindShorterRouteWithMoreSteps() {
    insertFlight("A", "B", 1000);
    insertFlight("A", "C", 1000);
    insertFlight("B", "D", 1000);
    insertFlight("C", "E", 3000);
    insertFlight("D", "E", 1000);
    insertFlight("E", "F", 1000);

    String response = get("/api/routes/A/F");

    assertJsonMap(response)
        .contains(
            entry("source", "A"),
            entry("destination", "F"),
            entry("distance", 4000));
    assertJsonList(response, "$.connections")
        .extracting("source", "destination", "distance", "type")
        .containsExactly(
            tuple("A", "B", 1000, "AIR"),
            tuple("B", "D", 1000, "AIR"),
            tuple("D", "E", 1000, "AIR"),
            tuple("E", "F", 1000, "AIR"));
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

  private void insertFlight(String source, String destination, long distance) {
    DatabaseTestMixin.State.jdbcTemplate.update("INSERT INTO flight(source, destination, distance) VALUES (?, ?, ?)", source, destination, distance);
  }
}
