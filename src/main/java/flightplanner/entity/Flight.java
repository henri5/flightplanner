package flightplanner.entity;

import static flightplanner.entity.ConnectionType.AIR;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Flight extends Connection {

  public Flight(AirportCode source, AirportCode destination, long distance) {
    super(source, destination, distance, AIR);
  }
}
