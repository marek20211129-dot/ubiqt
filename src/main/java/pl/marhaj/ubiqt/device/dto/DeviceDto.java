package pl.marhaj.ubiqt.device.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class DeviceDto {
    private String macAddress;
    private DeviceType deviceType;
}
