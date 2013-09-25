package org.openstack.atlas.api.validation.verifiers;

import org.openstack.atlas.docs.loadbalancers.api.v1.Node;
import org.openstack.atlas.api.validation.verifiers.Verifier;
import org.openstack.atlas.api.validation.verifiers.VerifierResult;
import org.openstack.atlas.docs.loadbalancers.api.v1.Nodes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DuplicateNodeVerifier implements Verifier<Nodes> {

    public VerifierResult verify(Nodes nodes) {
        Set<String> ipAddressesAndPorts = new HashSet<String>();
        if(nodes == null) {
            return new VerifierResult(true); // Don't flag the user for duplicate nodes when the nodes must not be empty verifier already flagged the user.
        }
        for (Node node : nodes.getNodes()) {
            String ipAddressAndPort = node.getAddress() + ":" + node.getPort();
            if (!ipAddressesAndPorts.add(ipAddressAndPort)) {
                return new VerifierResult(false);
            }
        }
        return new VerifierResult(true);
    }
}
