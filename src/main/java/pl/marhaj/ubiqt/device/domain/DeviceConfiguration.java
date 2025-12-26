package pl.marhaj.ubiqt.device.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DeviceConfiguration {

    @Bean
    DeviceFacade deviceFacade(DeviceRepository deviceRepository) {
        DeviceCreator deviceCreator = new DeviceCreator();
        TopologyCreator topologyCreator = new TopologyCreator();
        return new DeviceFacade(deviceRepository, deviceCreator, topologyCreator);
    }
}
