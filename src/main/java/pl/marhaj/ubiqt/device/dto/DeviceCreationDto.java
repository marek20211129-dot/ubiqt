package pl.marhaj.ubiqt.device.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.validation.constraints.Pattern;

@Getter
public class DeviceCreationDto {
    private DeviceType deviceType;
    @NotBlank
    @Pattern(regexp = "(?i)^[0-9a-fA-F]{2}(:[0-9a-fA-F]{2}){5}$",
            message = "macAddress must match aa:bb:cc:dd:ee:ff")
    private String macAddress;
    @Pattern(regexp = "(?i)^[0-9a-fA-F]{2}(:[0-9a-fA-F]{2}){5}$|null",
            message = "uplinkMacAddress must match aa:bb:cc:dd:ee:ff or be null")
    private String uplinkMacAddress;

    public DeviceCreationDto(DeviceType deviceType, String macAddress, String uplinkMacAddress) {
        this.deviceType = deviceType;
        this.macAddress = macAddress.toLowerCase();
        this.uplinkMacAddress = (null != uplinkMacAddress)?uplinkMacAddress.toLowerCase():null;
    }
}
