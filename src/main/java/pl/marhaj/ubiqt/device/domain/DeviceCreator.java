package pl.marhaj.ubiqt.device.domain;

import pl.marhaj.ubiqt.device.dto.DeviceCreationDto;

import static java.util.Objects.requireNonNull;

class DeviceCreator {

    Device from(DeviceCreationDto deviceDto) {
        requireNonNull(deviceDto);
        return Device.builder()
                .macAddress(deviceDto.getMacAddress())
                .deviceType(deviceDto.getDeviceType())
                .uplinkMacAddress(deviceDto.getUplinkMacAddress())
                .build();
    }
}
