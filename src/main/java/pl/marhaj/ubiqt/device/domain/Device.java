package pl.marhaj.ubiqt.device.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.marhaj.ubiqt.device.dto.DeviceDto;
import pl.marhaj.ubiqt.device.dto.DeviceType;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private DeviceType deviceType;

    @NotBlank
    @Column(nullable = false, length = 17)
    private String macAddress;

    @Column(length = 17)
    private String uplinkMacAddress;

    public DeviceDto dto() {
        return DeviceDto.builder()
                .macAddress(macAddress)
                .deviceType(deviceType)
                .build();
    }
}
