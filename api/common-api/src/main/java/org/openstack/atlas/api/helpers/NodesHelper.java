package org.openstack.atlas.api.helpers;

import org.openstack.atlas.adapter.helpers.IpHelper;
import org.openstack.atlas.service.domain.entities.LoadBalancer;
import org.openstack.atlas.service.domain.entities.Node;
import org.openstack.atlas.service.domain.entities.NodeCondition;
import org.openstack.atlas.service.domain.entities.NodeStatus;

import java.util.HashSet;
import java.util.Set;

public final class NodesHelper {

    public static void setNodesToStatus(LoadBalancer lb, NodeStatus status) {
        for (Node node : lb.getNodes()) {
            setNodeToStatus(status, node);
        }
    }

    /*
        Given a super-set of nodes and a subset of nodes this method will update the intersection on the loadbalancer
        with the super-set
     */
    public static void setNodesToStatus(LoadBalancer lbWithSubsetToChange, LoadBalancer lbWithSuperSet, NodeStatus status) {
        Set<String> ipAddressAndPorts = new HashSet<String>();

        for (Node node : lbWithSubsetToChange.getNodes()) {
            ipAddressAndPorts.add(IpHelper.createZeusIpString(node.getIpAddress(), node.getPort()));
        }

        for (Node node : lbWithSuperSet.getNodes()) {
            String ipAddressAndPort = IpHelper.createZeusIpString(node.getIpAddress(), node.getPort());

            if(ipAddressAndPorts.contains(ipAddressAndPort)) {
                setNodeToStatus(status, node);
            }
        }
    }

    private static void setNodeToStatus(NodeStatus status, Node node) {
        node.setStatus(status);
    }
}
