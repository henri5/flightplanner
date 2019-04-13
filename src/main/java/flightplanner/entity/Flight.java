package flightplanner.entity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Flight {

  private final String source;
  private final String destination;
  private final long distance;

  public Flight(String source, String destination, long distance) {
    this.source = source;
    this.destination = destination;
    this.distance = distance;
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
}
