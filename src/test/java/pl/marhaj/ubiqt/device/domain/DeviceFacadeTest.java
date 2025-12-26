package pl.marhaj.ubiqt.device.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pl.marhaj.ubiqt.device.dto.DeviceCreationDto;
import pl.marhaj.ubiqt.device.dto.DeviceDto;
import pl.marhaj.ubiqt.device.dto.DeviceType;
import pl.marhaj.ubiqt.device.dto.TopologyNode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DeviceFacadeTest {
    private DeviceRepository repository;
    private DeviceFacade facade;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDeviceRepository();
        facade = new DeviceFacade(repository, new DeviceCreator(), new TopologyCreator());
        repository.save(new Device(DeviceType.GATEWAY, "00:00:00:00:00:01", null));
        repository.save(new Device(DeviceType.SWITCH, "00:00:00:00:00:02", "00:00:00:00:00:01"));
        repository.save(new Device(DeviceType.ACCESS_POINT, "00:00:00:00:00:03", "00:00:00:00:00:02"));
        repository.save(new Device(DeviceType.SWITCH, "00:00:00:00:00:ff", "00:00:00:00:00:ee"));
    }

    @Test
    @DisplayName("List all devices sorted by device type")
    void listAllDevicesSortedByDeviceType() {
        List<DeviceDto> deviceDtos = facade.listSorted();

        assertThat(deviceDtos).extracting(DeviceDto::getDeviceType)
                .containsExactly(DeviceType.GATEWAY, DeviceType.SWITCH, DeviceType.SWITCH, DeviceType.ACCESS_POINT);
    }

    @Test
    @DisplayName("Get specific existing device")
    void getExistingDeviceByMac() {
        String mac = "00:00:00:00:00:03";
        DeviceDto deviceDto = facade.findOne(mac);

        assertThat(deviceDto).matches(device ->
                device.getMacAddress().equals(mac));
    }

    @Test
    @DisplayName("Get specific not existing device - NotFound")
    void getNonExistingMacDevice() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> facade.findOne("ff:ff:ff:ff:ff:ff"));

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Add device to tree")
    void addDeviceToTree() {
        String macAddress = "aa:aa:aa:aa:aa:aa";
        DeviceCreationDto deviceDto = DeviceCreationDto.builder()
                .deviceType(DeviceType.GATEWAY).macAddress(macAddress)
                .uplinkMacAddress(null).build();

        facade.addOne(deviceDto);

        List<DeviceDto> deviceDtos = facade.listSorted();

        assertThat(deviceDtos).hasSize(5).satisfiesOnlyOnce(
                device -> {
                    assertThat(device.getMacAddress()).isEqualTo(macAddress);
                });
    }

    @Test
    @DisplayName("Add device duplicate MAC - Conflict")
    void addDuplicateMacConflictHappen() {
        DeviceCreationDto deviceDto = DeviceCreationDto.builder()
                .deviceType(DeviceType.SWITCH).macAddress("00:00:00:00:00:02")
                .uplinkMacAddress("00:00:00:00:00:01").build();


        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> facade.addOne(deviceDto));
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

    }

    @Test
    @DisplayName("Add device same mac & uplinkmac - BadRequest")
    void addTheSameMacAndUplinkMacBadRequestHappen() {
        DeviceCreationDto deviceDto = DeviceCreationDto.builder()
                .deviceType(DeviceType.GATEWAY).macAddress("00:00:00:00:00:22")
                .uplinkMacAddress("00:00:00:00:00:22").build();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> facade.addOne(deviceDto));
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    @DisplayName("Get full topology with missing-uplink device as root")
    void getFullTressFromRoots() {
        List<TopologyNode> fullTopology = facade.getFullTopology();

        assertThat(fullTopology).extracting(TopologyNode::getMacAddress)
                .containsExactlyInAnyOrder("00:00:00:00:00:01", "00:00:00:00:00:ff");

        TopologyNode gateway = fullTopology.stream().filter(n -> n.getMacAddress().equals("00:00:00:00:00:01")).findFirst().orElseThrow();
        assertThat(gateway.getTopologyNodes()).extracting(TopologyNode::getMacAddress)
                .containsExactly("00:00:00:00:00:02");

        TopologyNode topolgySwitch = gateway.getTopologyNodes().stream().findFirst().orElseThrow();
        assertThat(topolgySwitch.getTopologyNodes()).extracting(TopologyNode::getMacAddress)
                .containsExactly("00:00:00:00:00:03");
    }

    @Test
    @DisplayName("Get topology from specific device")
    void getTopologyFromSpecificDevice() {
        TopologyNode subtree = facade.getTopologyFrom("00:00:00:00:00:02");
        assertThat(subtree.getMacAddress()).isEqualTo("00:00:00:00:00:02");
        assertThat(subtree.getTopologyNodes()).extracting(TopologyNode::getMacAddress)
                .containsExactly("00:00:00:00:00:03");
    }
}