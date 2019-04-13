package flightplanner.dao;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import flightplanner.entity.Flight;

@Component
@ParametersAreNonnullByDefault
public class FlightDao {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<Flight> getAll() {
    return jdbcTemplate.query("SELECT source, destination, distance FROM flight",
        (resultSet, i) -> new Flight(
            resultSet.getString("source"),
            resultSet.getString("destination"),
            resultSet.getLong("distance")));
  }
}
