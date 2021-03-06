package flightplanner.entity;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Route {

  private final AirportCode source;
  private final AirportCode destination;
  private final long distance;
  private final List<Connection> connections;

  public Route(AirportCode source, AirportCode destination, long distance, List<Connection> connections) {
    this.source = source;
    this.destination = destination;
    this.distance = distance;
    this.connections = connections;
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

  public List<Connection> getConnections() {
    return connections;
  }
}
