package flightplanner.integration;

import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.Test;

import flightplanner.integration.support.DatabaseTestMixin;
import flightplanner.integration.support.IntegrationTest;

public class RoutesWithLandConnectionsTest extends IntegrationTest {

  @Test
  public void canFindRouteWithDirectLandConnection() {
    insertLandConnection("A", "B", 100);

    String response = get("/api/routes/A/B?allowLandConnection=true");

    assertJsonMap(response)
        .contains(
            entry("source", "A"),
            entry("destination", "B"),
            entry("distance", 100));
    assertJsonList(response, "$.connections")
        .extracting("source", "destination", "distance", "type")
        .containsExactly(
            tuple("A", "B", 100, "LAND"));
  }

  @Test
  public void canFindRouteWithLandConnectionAsRequiredStep() {
    insertFlight("A", "B", 1000);
    insertLandConnection("B", "C", 100);
    insertFlight("C", "D", 1000);

    String response = get("/api/routes/A/D?allowLandConnection=true");

    assertJsonMap(response)
        .contains(
            entry("source", "A"),
            entry("destination", "D"),
            entry("distance", 2100));
    assertJsonList(response, "$.connections")
        .extracting("source", "destination", "distance", "type")
        .containsExactly(
            tuple("A", "B", 1000, "AIR"),
            tuple("B", "C", 100, "LAND"),
            tuple("C", "D", 1000, "AIR"));
  }

  @Test
  public void failsToFindRouteWithLandConnectionIfLandConnectionNotEnabled() {
    insertFlight("A", "B", 1000);
    insertLandConnection("B", "C", 100);
    insertFlight("C", "D", 1000);

    String response = get("/api/routes/A/D");

    assertJsonMap(response)
        .containsExactly(
            entry("message", "Route not found"));
  }

  private void insertFlight(String source, String destination, long distance) {
    DatabaseTestMixin.State.jdbcTemplate.update("INSERT INTO flight(source, destination, distance) VALUES (?, ?, ?)", source, destination, distance);
  }

  private void insertLandConnection(String airport1, String airport2, long distance) {
    DatabaseTestMixin.State.jdbcTemplate.update("INSERT INTO land_connection(airport_1, airport_2, distance) VALUES (?, ?, ?)", airport1, airport2, distance);
  }
}
