package pl.marhaj.ubiqt.device.domain;

import org.springframework.data.repository.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

interface DeviceRepository extends Repository<Device, String> {
    void deleteAll();

    Device save(Device device);

    List<Device> findAll();

    Optional<Device> findByMacAddress(String macAddress);

    boolean existsByMacAddress(String macAddress);

    default Device findOneOrThrow(String macAddress) {
        return findByMacAddress(macAddress)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found: " + macAddress));
    }
}
