package flightplanner.web;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import flightplanner.entity.AirportCode;
import flightplanner.entity.Route;
import flightplanner.service.RouteService;

@RestController
public class RouteController {

  @Autowired
  private RouteService service;

  @GetMapping("api/routes/{source}/{destination}")
  public Object getRoute(@PathVariable("source") AirportCode source, @PathVariable("destination") AirportCode destination) {
    Optional<Route> route = service.findBestRoute(source, destination);

    if (route.isPresent()) {
      return route;
    }

    return Map.of("message", "Route not found");
  }
}
