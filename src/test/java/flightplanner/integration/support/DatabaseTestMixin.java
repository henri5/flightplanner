package flightplanner.integration.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public interface DatabaseTestMixin {

  @Autowired
  default void injectJdbcTemplate(JdbcTemplate jdbcTemplate) {
    State.jdbcTemplate = jdbcTemplate;
  }

  class State {
    public static JdbcTemplate jdbcTemplate;

    static void setProperties() {
      System.setProperty("spring.datasource.url", "jdbc:tc:postgresql:9.6.5://localhost/test");
      System.setProperty("spring.datasource.driver-class-name", "org.testcontainers.jdbc.ContainerDatabaseDriver");
    }
  }
}
