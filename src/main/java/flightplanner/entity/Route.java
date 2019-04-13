package flightplanner.entity;

import java.util.List;

public class Route {

  private final String source;
  private final String destination;
  private final long distance;
  private final List<Flight> flights;

  public Route(String source, String destination, long distance, List<Flight> flights) {
    this.source = source;
    this.destination = destination;
    this.distance = distance;
    this.flights = flights;
  }

  public String getSource() {
    return source;
  }

  public String getDestination() {
    return destination;
  }

  public long getDistance() {
    return distance;
  }

  public List<Flight> getFlights() {
    return flights;
  }
}
