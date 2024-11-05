package by.dudkin.driver.service;

import by.dudkin.common.util.ErrorMessages;
import by.dudkin.driver.domain.Driver;
import by.dudkin.driver.mapper.DriverMapper;
import by.dudkin.driver.repository.DriverRepository;
import by.dudkin.driver.rest.advice.DriverNotFoundException;
import by.dudkin.driver.rest.dto.request.DriverRequest;
import by.dudkin.driver.rest.dto.response.DriverResponse;
import by.dudkin.driver.service.api.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Alexander Dudkin
 */
@Service
@Transactional
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverMapper driverMapper;
    private final DriverRepository driverRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<DriverResponse> findAll(Pageable pageable) {
        return driverRepository.findAll(pageable).map(driverMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponse findById(long carId) {
        return driverMapper.toResponse(getOrThrow(carId));
    }

    @Override
    public DriverResponse create(DriverRequest driverRequest) {
        return driverMapper.toResponse(driverRepository.save(driverMapper.toDriver(driverRequest)));
    }

    @Override
    public DriverResponse update(long driverId, DriverRequest driverRequest) {
        Driver targetDriver = getOrThrow(driverId);
        driverMapper.updateDriver(driverRequest, targetDriver);
        driverRepository.save(targetDriver);
        return driverMapper.toResponse(targetDriver);
    }

    @Override
    public void delete(long driverId) {
        Driver driver = getOrThrow(driverId);
        driverRepository.delete(driver);
    }

    private Driver getOrThrow(long driverId) {
        return driverRepository.findWithAssignmentsAndCarsById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(ErrorMessages.DRIVER_NOT_FOUND));
    }

}