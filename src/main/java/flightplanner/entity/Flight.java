package flightplanner.entity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Flight {

  private final AirportCode source;
  private final AirportCode destination;
  private final long distance;

  public Flight(AirportCode source, AirportCode destination, long distance) {
    this.source = source;
    this.destination = destination;
    this.distance = distance;
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
}
