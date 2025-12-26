package pl.marhaj.ubiqt.device.domain;

import pl.marhaj.ubiqt.device.dto.TopologyNode;

import java.util.*;
import java.util.stream.Collectors;

class TopologyCreator {

    TopologyNode buildTree(String mac,
                           Map<String, List<Device>> childrenByParent,
                           Set<String> visited) {
        if (!visited.add(mac)) {
            // cycle detected; stop expanding to avoid infinite loop
            return new TopologyNode(mac);
        }
        TopologyNode node = new TopologyNode(mac);
        List<Device> children = childrenByParent.getOrDefault(mac, List.of());
        for (Device child : children) {
            node.addChild(buildTree(child.getMacAddress(), childrenByParent, visited));
        }
        visited.remove(mac); // allow same node to appear in other branches when building forest
        return node;
    }


    List<TopologyNode> buildFullTrees(List<Device> devices) {
        Map<String, TopologyNode> nodeByMac = new HashMap<>();
        Map<String, Device> deviceByMac = new HashMap<>();
        Map<String, List<Device>> childrenByParent = new HashMap<>();

        for (Device d : devices) {
            nodeByMac.put(d.getMacAddress(), new TopologyNode(d.getMacAddress()));
            deviceByMac.put(d.getMacAddress(), d);
        }

        for (Device d : devices) {
            String parent = d.getUplinkMacAddress();
            if (parent != null) {
                childrenByParent.computeIfAbsent(parent.toLowerCase(), k -> new ArrayList<>()).add(d);
            }
        }

        Set<String> roots = devices.stream()
                .filter(d -> d.getUplinkMacAddress() == null || !deviceByMac.containsKey(d.getUplinkMacAddress().toLowerCase()))
                .map(Device::getMacAddress)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Build each tree starting from root
        List<TopologyNode> allTrees = new ArrayList<>();
        for (String rootMac : roots) {
            allTrees.add(buildTree(rootMac, childrenByParent, new HashSet<>()));
        }
        return allTrees;
    }
}
