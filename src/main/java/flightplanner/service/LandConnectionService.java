package flightplanner.service;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import flightplanner.dao.LandConnectionDao;
import flightplanner.entity.LandConnection;

@Component
@ParametersAreNonnullByDefault
public class LandConnectionService {

  @Autowired
  private LandConnectionDao dao;

  public List<LandConnection> getAll() {
    return dao.getAll().stream()
        .flatMap(landConnection -> Stream.of(landConnection, landConnection.reversed()))
        .collect(toUnmodifiableList());
  }
}
