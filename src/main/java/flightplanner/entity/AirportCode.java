package flightplanner.entity;

import java.util.Objects;

import javax.annotation.ParametersAreNonnullByDefault;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

@ParametersAreNonnullByDefault
public class AirportCode {

  private final String code;

  @JsonCreator
  public AirportCode(String code) {
    this.code = code;
  }

  @JsonValue
  public String getCode() {
    return code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AirportCode that = (AirportCode) o;
    return code.equals(that.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }
}
