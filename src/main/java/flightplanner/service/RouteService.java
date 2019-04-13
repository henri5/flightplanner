package flightplanner.service;

import static java.lang.Long.MAX_VALUE;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

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

  public Optional<Route> findBestRoute(String source, String destination) {
    Map<String, List<Flight>> flights = flightDao.getAll().stream()
        .sorted(comparingLong(Flight::getDistance))
        .collect(groupingBy(Flight::getSource, toList()));

    Set<String> unvisited = flights.values().stream()
        .flatMap(Collection::stream)
        .flatMap(flight -> Stream.of(flight.getSource(), flight.getDestination()))
        .collect(toSet());

    Map<String, Route> routes = new HashMap<>();
    String current = source;

    while (true) {
      Route routeToCurrent = Optional.ofNullable(routes.get(current))
          .orElse(new Route(source, current, 0, List.of()));

      Optional.ofNullable(flights.get(current)).orElse(List.of()).forEach(flight -> {
        Route route = Optional.ofNullable(routes.get(flight.getDestination())).orElse(new Route(source, flight.getDestination(), MAX_VALUE, List.of()));

        if (route.getDistance() > flight.getDistance() + routeToCurrent.getDistance()) {
          List<Flight> newFlights = new ArrayList<>(routeToCurrent.getFlights());
          newFlights.add(flight);

          routes.put(
              flight.getDestination(),
              new Route(source, flight.getDestination(), flight.getDistance() + routeToCurrent.getDistance(), unmodifiableList(newFlights)));
        }
      });

      if (current.equals(destination)) {
        return Optional.of(routes.get(destination));
      }

      unvisited.remove(current);

      Optional<String> nextLocation = routes.entrySet().stream()
          .filter(entry -> unvisited.contains(entry.getKey()))
          .filter(entry -> entry.getValue().getFlights().size() <= 3)
          .min(comparingLong(entry -> entry.getValue().getDistance()))
          .map(Map.Entry::getKey);

      if (nextLocation.isEmpty()) {
        return Optional.ofNullable(routes.get(destination));
      }

      current = nextLocation.get();
    }
  }
}
