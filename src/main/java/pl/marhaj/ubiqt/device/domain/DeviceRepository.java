package pl.marhaj.ubiqt.device.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

interface DeviceRepository extends JpaRepository<Device, String> {
    Optional<Device> findByMacAddress(String macAddress);
    boolean existsByMacAddress(String macAddress);

    default Device findOneOrThrow(String macAddress) {
        return findByMacAddress(macAddress)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found: " + macAddress));
    }
}
