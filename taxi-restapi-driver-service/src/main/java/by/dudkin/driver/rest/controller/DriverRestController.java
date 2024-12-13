package by.dudkin.driver.rest.controller;

import by.dudkin.common.util.PaginatedResponse;
import by.dudkin.driver.rest.api.DriverApi;
import by.dudkin.driver.rest.dto.request.DriverRequest;
import by.dudkin.driver.rest.dto.response.DriverResponse;
import by.dudkin.driver.service.api.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author Alexander Dudkin
 */
@RestController
@RequiredArgsConstructor
@CrossOrigin(exposedHeaders = "errors, content-type")
public class DriverRestController implements DriverApi {

    private final DriverService driverService;

    @Override
    public ResponseEntity<DriverResponse> get(long driverId) {
        return new ResponseEntity<>(driverService.findById(driverId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<PaginatedResponse<DriverResponse>> getAll(Pageable pageable) {
        return new ResponseEntity<>(driverService.findAll(pageable), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DriverResponse> save(DriverRequest driverRequest) {
        return new ResponseEntity<>(driverService.create(driverRequest), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<DriverResponse> update(long driverId, DriverRequest driverRequest) {
        return new ResponseEntity<>(driverService.update(driverId, driverRequest), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> delete(long driverId) {
        driverService.delete(driverId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<DriverResponse> markAvailable(long driverId) {
        return new ResponseEntity<>(driverService.markAvailable(driverId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DriverResponse> markOnTrip(long driverId) {
        return new ResponseEntity<>(driverService.markBusy(driverId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<DriverResponse> markOffline(long driverId) {
        return new ResponseEntity<>(driverService.markOffline(driverId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateBalance(long driverId, BigDecimal amount) {
        driverService.updateBalance(driverId, amount);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
