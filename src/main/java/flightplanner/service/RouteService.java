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
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import flightplanner.dao.FlightDao;
import flightplanner.entity.AirportCode;
import flightplanner.entity.Flight;
import flightplanner.entity.Route;

@Component
@ParametersAreNonnullByDefault
public class RouteService {

  @Autowired
  private FlightDao flightDao;

  public Optional<Route> findBestRoute(AirportCode source, AirportCode destination) {
    Map<AirportCode, List<Flight>> flights = flightDao.getAll().stream()
        .sorted(comparingLong(Flight::getDistance))
        .collect(groupingBy(Flight::getSource, toList()));

    Set<AirportCode> unvisited = flights.values().stream()
        .flatMap(Collection::stream)
        .flatMap(flight -> Stream.of(flight.getSource(), flight.getDestination()))
        .collect(toSet());

    Map<AirportCode, Route> routes = new HashMap<>();
    AirportCode current = source;

    while (true) {
      Route routeToCurrent = Optional.ofNullable(routes.get(current))
          .orElse(new Route(source, current, 0, List.of()));

      flights.getOrDefault(current, List.of()).stream()
          .map(flight -> tryFindShorterRoute(routes, flight, routeToCurrent))
          .flatMap(Optional::stream)
          .forEach(route -> routes.put(route.getDestination(), route));

      if (current.equals(destination)) {
        return Optional.ofNullable(routes.get(destination));
      }

      unvisited.remove(current);

      Optional<AirportCode> nextLocation = routes.entrySet().stream()
          .filter(entry -> unvisited.contains(entry.getKey()))
          .filter(entry -> entry.getValue().getFlights().size() <= 3)
          .min(comparingLong(entry -> entry.getValue().getDistance()))
          .map(Entry::getKey);

      if (nextLocation.isEmpty()) {
        return Optional.ofNullable(routes.get(destination));
      }

      current = nextLocation.get();
    }
  }

  private Optional<Route> tryFindShorterRoute(Map<AirportCode, Route> routes, Flight flight, Route routeToCurrent) {
    Route route = Optional.ofNullable(routes.get(flight.getDestination())).orElse(new Route(routeToCurrent.getSource(), flight.getDestination(), MAX_VALUE, List.of()));

    if (route.getDistance() > flight.getDistance() + routeToCurrent.getDistance()) {
      List<Flight> newFlights = new ArrayList<>(routeToCurrent.getFlights());
      newFlights.add(flight);

      return Optional.of(new Route(
          routeToCurrent.getSource(),
          flight.getDestination(),
          flight.getDistance() + routeToCurrent.getDistance(),
          unmodifiableList(newFlights)));
    }

    return Optional.empty();
  }
}
