package pl.marhaj.ubiqt.device;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.marhaj.ubiqt.device.domain.DeviceFacade;
import pl.marhaj.ubiqt.device.dto.DeviceCreationDto;
import pl.marhaj.ubiqt.device.dto.DeviceDto;
import pl.marhaj.ubiqt.device.dto.TopologyNode;

import java.util.List;

@RestController
@RequiredArgsConstructor
class DeviceController {
    private final DeviceFacade deviceFacade;

    @PostMapping("/devices")
    @ResponseStatus(HttpStatus.CREATED)
    DeviceDto save(@Valid @RequestBody DeviceCreationDto deviceDto) {
        return deviceFacade.addOne(deviceDto);
    }

    @GetMapping("/devices/{macAddress}")
    DeviceDto getDevice(@PathVariable String macAddress) {
        return deviceFacade.findOne(macAddress);
    }

    @GetMapping("/devices")
    List<DeviceDto> getDevices() {
        return deviceFacade.listSorted();
    }

    @GetMapping("/topology")
    List<TopologyNode> getFullTopology() {
        return deviceFacade.getFullTopology();
    }

    @GetMapping("/topology/{macAddress}")
    TopologyNode getTopologyFrom(@PathVariable String macAddress) {
        return deviceFacade.getTopologyFrom(macAddress);
    }
}
