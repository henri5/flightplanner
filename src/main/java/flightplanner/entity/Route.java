package flightplanner.entity;

import java.util.List;

public class Route {

  private final AirportCode source;
  private final AirportCode destination;
  private final long distance;
  private final List<Flight> flights;

  public Route(AirportCode source, AirportCode destination, long distance, List<Flight> flights) {
    this.source = source;
    this.destination = destination;
    this.distance = distance;
    this.flights = flights;
  }

  public AirportCode getSource() {
    return source;
  }

  public AirportCode getDestination() {
    return destination;
  }

  public long getDistance() {
    return distance;
  }

  public List<Flight> getFlights() {
    return flights;
  }
}
