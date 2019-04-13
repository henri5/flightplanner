package flightplanner.service;

import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import flightplanner.dao.FlightDao;
import flightplanner.entity.Flight;
import flightplanner.entity.Route;

@Component
@ParametersAreNonnullByDefault
public class RouteService {

  @Autowired
  private FlightDao flightDao;

  public Route findBestRoute(String source, String destination) {
    Map<String, List<Flight>> flights = flightDao.getAll().stream()
        .sorted(comparingLong(Flight::getDistance))
        .collect(groupingBy(Flight::getSource, toList()));

    Set<String> unvisited = new HashSet<>(flights.keySet());

    Map<String, Route> routes = new HashMap<>();
    String current = source;

    while (true) {
      Route routeToCurrent = Optional.ofNullable(routes.get(current))
          .orElse(new Route(source, current, 0, List.of()));

      flights.get(current).forEach(flight -> {
        Route route = Optional.ofNullable(routes.get(flight.getDestination()))
            .orElse(new Route(source, flight.getDestination(), Long.MAX_VALUE, List.of()));

        if (route.getDistance() > flight.getDistance() + routeToCurrent.getDistance()) {
          List<Flight> newFlights = new ArrayList<>(routeToCurrent.getFlights());
          newFlights.add(flight);

          routes.put(
              flight.getDestination(),
              new Route(source, flight.getDestination(), flight.getDistance() + routeToCurrent.getDistance(), unmodifiableList(newFlights)));
        }
      });

      if (current.equals(destination)) {
        return routes.get(destination);
      }

      unvisited.remove(current);

      current = routes.entrySet().stream()
          .filter(entry -> unvisited.contains(entry.getKey()))
          .min(comparingLong(entry -> entry.getValue().getDistance()))
          .orElseThrow()
          .getKey();
    }
  }
}
