package by.dudkin.driver.rest.controller;

import by.dudkin.driver.rest.dto.response.DriverResponse;
import by.dudkin.driver.rest.dto.response.PaginatedResponse;
import by.dudkin.driver.util.TestDataGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alexander Dudkin
 */
@Testcontainers
@Sql("classpath:data.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DriverRestControllerTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0");

    @Autowired
    TestRestTemplate restTemplate;

    private static final String DRIVERS_URI = "/drivers";
    private static final String BOB_FIRSTNAME = "Bob";

    @Test
    @SuppressWarnings("unchecked")
    void shouldFindAllDrivers() {
        // Act
        PaginatedResponse<DriverResponse> drivers = restTemplate.getForObject(DRIVERS_URI, PaginatedResponse.class);

        // Assert
        assertThat(drivers).isNotNull();
        assertThat(drivers.getContent().size()).isGreaterThan(3);
        assertThat(drivers.getContent().size()).isLessThan(999);
    }

    @Test
    void shouldFindDriverWhenValidDriverID() {
        // Arrange
        var URI = "%s/%d".formatted(DRIVERS_URI, 103L);

        // Act
        ResponseEntity<DriverResponse> response = restTemplate.exchange(URI, HttpMethod.GET, null, DriverResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().info().getFirstName()).isEqualTo(BOB_FIRSTNAME);
    }

    @Test
    @Rollback
    void shouldCreateDriver() {
        // Arrange
        var request = TestDataGenerator.randomDriverRequest();

        // Act
        ResponseEntity<DriverResponse> response = restTemplate.exchange(DRIVERS_URI, HttpMethod.POST, new HttpEntity<>(request), DriverResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().info().getPhone()).isNotEmpty();
    }

    @Test
    @Rollback
    void shouldUpdateDriver() {
        // Arrange
        var request = TestDataGenerator.randomDriverRequestWithFirstname(BOB_FIRSTNAME);
        var URI = "%s/%d".formatted(DRIVERS_URI, 102L);

        // Act
        ResponseEntity<DriverResponse> response = restTemplate.exchange(URI, HttpMethod.PUT, new HttpEntity<>(request), DriverResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().info().getFirstName()).isEqualTo(BOB_FIRSTNAME);
    }

    @Test
    @Rollback
    void shouldDeleteDriver() {
        // Arrange
        var URI = "%s/%d".formatted(DRIVERS_URI, 102L);

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(URI, HttpMethod.DELETE, null, Void.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

}
