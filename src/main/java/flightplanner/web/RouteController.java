package flightplanner.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import flightplanner.service.RouteService;

@RestController
public class RouteController {

  @Autowired
  private RouteService service;

  @GetMapping("api/routes/{source}/{destination}")
  public Object getRoute(@PathVariable("source") String source, @PathVariable("destination") String destination) {
    return service.findBestRoute(source, destination);
  }
}
