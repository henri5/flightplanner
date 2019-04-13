package flightplanner.integration.support;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;

import org.assertj.core.api.ListAssert;
import org.assertj.core.api.MapAssert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonContentAssert;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class IntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  static {
    System.setProperty("spring.datasource.url", "jdbc:tc:postgresql:9.6.5://localhost/test");
    System.setProperty("spring.datasource.driver-class-name", "org.testcontainers.jdbc.ContainerDatabaseDriver");
  }

  public MapAssert<Object, Object> assertJsonMap(String json) {
    return new JsonContentAssert(String.class, json).extractingJsonPathMapValue("$");
  }

  public ListAssert<Object> assertJsonList(String json, String expression) {
    return new JsonContentAssert(String.class, json).extractingJsonPathArrayValue(expression);
  }

  public String get(String path) {
    ResponseEntity<String> response = makeRequest(path);
    assertEquals(OK, response.getStatusCode());
    return response.getBody();
  }

  public ResponseEntity<String> makeRequest(String path) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<>(headers), String.class);
  }
}
