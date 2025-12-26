package pl.marhaj.ubiqt.device.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TopologyNode {
    private String macAddress;
    private List<TopologyNode> topologyNodes = new ArrayList<>();

    public TopologyNode(String macAddress) {
        this.macAddress = macAddress;
    }
    public void addChild(TopologyNode child) {
        topologyNodes.add(child);
    }
}

