package flightplanner.integration.support;

public interface DatabaseCleanupTestMixin extends DatabaseTestMixin {

  default void databaseCleanup() {
    DatabaseTestMixin.State.jdbcTemplate.update("DELETE FROM flight");
    DatabaseTestMixin.State.jdbcTemplate.update("DELETE FROM land_connection");
  }
}
