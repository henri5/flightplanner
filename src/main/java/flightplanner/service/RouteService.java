package flightplanner.service;

import static java.lang.Long.MAX_VALUE;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableList;

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
import flightplanner.entity.Connection;
import flightplanner.entity.Route;

@Component
@ParametersAreNonnullByDefault
public class RouteService {

  @Autowired
  private FlightDao flightDao;

  @Autowired
  private LandConnectionService landConnectionService;

  public Optional<Route> findBestRoute(AirportCode source, AirportCode destination, boolean allowLandConnection) {
    Map<AirportCode, List<Connection>> connections = getConnections(allowLandConnection);
    Set<AirportCode> unvisited = getAirports(connections);

    Map<AirportCode, Route> routes = new HashMap<>();
    AirportCode current = source;

    while (true) {
      Route routeToCurrent = Optional.ofNullable(routes.get(current))
          .orElse(new Route(source, current, 0, List.of()));

      connections.getOrDefault(current, List.of()).stream()
          .map(connection -> tryFindShorterRoute(routes, connection, routeToCurrent))
          .flatMap(Optional::stream)
          .forEach(route -> routes.put(route.getDestination(), route));

      if (current.equals(destination)) {
        return Optional.ofNullable(routes.get(destination));
      }

      unvisited.remove(current);

      Optional<AirportCode> nextLocation = routes.entrySet().stream()
          .filter(entry -> unvisited.contains(entry.getKey()))
          .filter(entry -> entry.getValue().getConnections().size() <= 3)
          .min(comparingLong(entry -> entry.getValue().getDistance()))
          .map(Entry::getKey);

      if (nextLocation.isEmpty()) {
        return Optional.ofNullable(routes.get(destination));
      }

      current = nextLocation.get();
    }
  }

  private Map<AirportCode, List<Connection>> getConnections(boolean allowLandConnections) {
    return Stream.concat(flightDao.getAll().stream(), allowLandConnections ? landConnectionService.getAll().stream() : Stream.empty())
        .sorted(comparingLong(Connection::getDistance))
        .collect(groupingBy(Connection::getSource, toUnmodifiableList()));
  }

  private Set<AirportCode> getAirports(Map<AirportCode, List<Connection>> connections) {
    return connections.values().stream()
        .flatMap(Collection::stream)
        .flatMap(flight -> Stream.of(flight.getSource(), flight.getDestination()))
        .collect(toSet());
  }

  private Optional<Route> tryFindShorterRoute(Map<AirportCode, Route> routes, Connection connection, Route routeToCurrent) {
    Route route = Optional.ofNullable(routes.get(connection.getDestination()))
        .orElse(new Route(routeToCurrent.getSource(), connection.getDestination(), MAX_VALUE, List.of()));

    if (route.getDistance() > connection.getDistance() + routeToCurrent.getDistance()) {
      List<Connection> connections = new ArrayList<>(routeToCurrent.getConnections());
      connections.add(connection);

      return Optional.of(new Route(
          routeToCurrent.getSource(),
          connection.getDestination(),
          connection.getDistance() + routeToCurrent.getDistance(),
          unmodifiableList(connections)));
    }

    return Optional.empty();
  }
}
