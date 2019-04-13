package flightplanner.entity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Connection {

  private final AirportCode source;
  private final AirportCode destination;
  private final long distance;
  private final ConnectionType type;

  public Connection(AirportCode source, AirportCode destination, long distance, ConnectionType type) {
    this.source = source;
    this.destination = destination;
    this.distance = distance;
    this.type = type;
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

  public ConnectionType getType() {
    return type;
  }
}
