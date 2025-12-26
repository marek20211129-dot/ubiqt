package pl.marhaj.ubiqt.device.domain;

import pl.marhaj.ubiqt.device.dto.DeviceType;

import java.util.Map;

interface PiorityOrder {
    Map<DeviceType,Integer> order();
}
