package pl.marhaj.ubiqt.device.domain;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDeviceRepository implements DeviceRepository {
    private ConcurrentHashMap<String, Device> devicesMap = new ConcurrentHashMap<>();

    @Override
    public void deleteAll() {
        devicesMap.clear();
    }

    @Override
    public Device save(Device device) {
        return devicesMap.compute(device.getMacAddress(), (key, val)
                -> val = device);
    }

    @Override
    public List<Device> findAll() {
        return devicesMap.values().stream().toList();
    }

    @Override
    public Optional<Device> findByMacAddress(String macAddress) {
        return Optional.ofNullable(devicesMap.get(macAddress));
    }

    @Override
    public boolean existsByMacAddress(String macAddress) {
        return devicesMap.containsKey(macAddress);
    }
}
