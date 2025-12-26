package pl.marhaj.ubiqt.device.domain;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.marhaj.ubiqt.device.dto.DeviceCreationDto;
import pl.marhaj.ubiqt.device.dto.DeviceDto;
import pl.marhaj.ubiqt.device.dto.DeviceType;
import pl.marhaj.ubiqt.device.dto.TopologyNode;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
public class DeviceFacade {
    // Piority order  GATEWAY > SWITCH > ACCESS_POINT
    private static final Map<DeviceType, Integer> sortingOrder = Map.of(
            DeviceType.GATEWAY, 0,
            DeviceType.SWITCH, 1,
            DeviceType.ACCESS_POINT, 2);

    private final DeviceRepository deviceRepository;
    private final DeviceCreator deviceCreator;
    private final TopologyCreator topologyCreator;

    public DeviceDto addOne(DeviceCreationDto deviceDto) {
        requireNotNull(deviceDto);
        verifyCollisions(deviceDto);

        Device device = deviceCreator.from(deviceDto);
        device = deviceRepository.save(device);

        return device.dto();
    }

    public DeviceDto findOne(String mac) {
        requireNotNull(mac);
        String searchMac = mac.toLowerCase();
        Device device = deviceRepository.findOneOrThrow(searchMac);
        return device.dto();
    }

    public List<DeviceDto> listSorted() {
        return deviceRepository.findAll().stream()
                .collect(Collectors.groupingBy(Device::getDeviceType)).entrySet().stream()
                .sorted(Comparator.comparing(listEntry -> sortingOrder.get(listEntry.getKey())))
                .flatMap(devices -> devices.getValue().stream()).map(device -> device.dto()).toList();
    }

    public TopologyNode getTopologyFrom(String mac) {
        requireNotNull(mac);
        String rootMac = mac.toLowerCase();
        Device root = deviceRepository.findByMacAddress(rootMac)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Device not found: " + rootMac));

        // Build adjacency map: parentMac -> children list
        Map<String, List<Device>> childrenByParent = deviceRepository.findAll().stream()
                .filter(d -> d.getUplinkMacAddress() != null)
                .collect(Collectors.groupingBy(d -> d.getUplinkMacAddress()));

        return topologyCreator.buildTree(root.getMacAddress(), childrenByParent, new HashSet<>());
    }

    public List<TopologyNode> getFullTopology() {
        List<Device> devices = deviceRepository.findAll();
        return topologyCreator.buildFullTrees(devices);
    }

    private static <T> void requireNotNull(T object) {
        if (null == object) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input value cannot be null");
        }
    }

    private void verifyCollisions(DeviceCreationDto deviceDto) {
        final String macAddress = deviceDto.getMacAddress();
        final String uplinkMacAddress = deviceDto.getUplinkMacAddress();

        if (deviceRepository.existsByMacAddress(macAddress)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Device already exists: " + macAddress);
        }
        if (null != uplinkMacAddress && uplinkMacAddress.equals(macAddress)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "uplinkMacAddress cannot equal macAddress");
        }
    }
}
