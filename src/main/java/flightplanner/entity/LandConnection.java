package flightplanner.entity;

import static flightplanner.entity.ConnectionType.LAND;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LandConnection extends Connection {

  public LandConnection(AirportCode source, AirportCode destination, long distance) {
    super(source, destination, distance, LAND);
  }

  public LandConnection reversed() {
    return new LandConnection(getDestination(), getSource(), getDistance());
  }
}
