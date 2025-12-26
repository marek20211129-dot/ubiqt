package pl.marhaj.ubiqt.device.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.marhaj.ubiqt.device.exception.DeviceNotFoundException;

interface DeviceRepository extends JpaRepository<Device, String> {
    Device findByMacAddress(String mac);
    boolean existsByMacAddress(String macAddress);

    default Device findOneOrThrow(String mac) {
        Device device = findByMacAddress(mac);
        if(null == device) {
            throw new DeviceNotFoundException(mac);
        }
        return device;
    }
}
