package flightplanner.dao;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import flightplanner.entity.AirportCode;
import flightplanner.entity.Flight;
import flightplanner.entity.LandConnection;

@Component
@ParametersAreNonnullByDefault
public class LandConnectionDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<LandConnection> getAll() {
    return jdbcTemplate.query("SELECT airport_1, airport_2, distance FROM land_connection",
        (resultSet, i) -> new LandConnection(
            new AirportCode(resultSet.getString("airport_1")),
            new AirportCode(resultSet.getString("airport_2")),
            resultSet.getLong("distance")));
  }
}
