package org.openstack.atlas.datamodel;

import java.util.HashSet;
import java.util.Set;

public class CoreNodeStatus implements NodeStatus {
    public static final String ONLINE = "ONLINE";
    public static final String OFFLINE = "OFFLINE";
    protected static final Set<String> nodeStatuses;

    static {
        nodeStatuses = new HashSet<String>();
        nodeStatuses.add(ONLINE);
        nodeStatuses.add(OFFLINE);
    }

    public CoreNodeStatus() {
    }

    public boolean contains(String str) {
        boolean out;
        out = nodeStatuses.contains(str);
        return out;
    }

    public static String[] values() {
        return nodeStatuses.toArray(new String[nodeStatuses.size()]);
    }

    @Override
    public String[] toList() {
        return nodeStatuses.toArray(new String[nodeStatuses.size()]);
    }
}
