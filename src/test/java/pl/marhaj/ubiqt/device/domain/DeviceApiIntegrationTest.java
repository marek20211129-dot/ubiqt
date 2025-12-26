package pl.marhaj.ubiqt.device.domain;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;
import pl.marhaj.ubiqt.device.dto.DeviceDto;
import pl.marhaj.ubiqt.device.dto.DeviceType;

import java.util.List;

import static org.junit.Assert.assertEquals;

@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeviceApiIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private DeviceRepository repository;

    @Autowired
    private RestTestClient client;

    @BeforeEach
    void setup() {
        repository.deleteAll();
        repository.save(new Device(DeviceType.GATEWAY, "00:00:00:00:00:01", null));
        repository.save(new Device(DeviceType.SWITCH, "00:00:00:00:00:02", "00:00:00:00:00:01"));
        repository.save(new Device(DeviceType.ACCESS_POINT, "00:00:00:00:00:03", "00:00:00:00:00:02"));
        repository.save(new Device(DeviceType.SWITCH, "00:00:00:00:00:ff", "00:00:00:00:00:ee"));
    }

    @Test
    @DisplayName("POST /devices -201 CREATED")
    void addNewDevice() {
        String newDevicePayload = """
                {"deviceType":"GATEWAY","macAddress":"aa:aa:aa:aa:aa:aa","uplinkMacAddress":null}
                """;

        client.post().uri("/devices").body(newDevicePayload).header("Content-Type", "application/json")
                .exchange().expectStatus().isCreated();
    }

    @Test
    @DisplayName("POST /devices -409 CONFLICT")
    void addDuplicateMacAddressConflict() {
        String newDevicePayload = """
                {"deviceType":"GATEWAY","macAddress":"00:00:00:00:00:03","uplinkMacAddress":null}
                """;

        client.post().uri("/devices").body(newDevicePayload).header("Content-Type", "application/json")
                .exchange().expectStatus().is4xxClientError().expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("GET /devices -200 CREATED")
    void listAllDevice() {
        List<DeviceDto> responseBody = client.get().uri("/devices")
                .exchange().expectStatus().isOk().expectBody(new ParameterizedTypeReference<List<DeviceDto>>() {
                }).returnResult().getResponseBody();

        assertEquals(4, responseBody.size());
        assertEquals(DeviceType.GATEWAY, responseBody.get(0).getDeviceType());
        assertEquals(DeviceType.ACCESS_POINT, responseBody.get(3).getDeviceType());
    }

}
