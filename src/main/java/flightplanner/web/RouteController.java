package flightplanner.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouteController {

  @GetMapping("api/routes/{from}/{to}")
  public Object getRoute(@PathVariable("from") String from, @PathVariable("to") String to) {
    return String.format("Flying from %s to %s", from, to);
  }
}
