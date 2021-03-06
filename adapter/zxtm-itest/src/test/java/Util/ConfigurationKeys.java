package Util;

import org.openstack.atlas.cfg.ConfigurationKey;


public enum ConfigurationKeys implements ConfigurationKey {
    zxtm_username,
    zxtm_password,
    zxtm_endpoint_uri,
    target_host,
    failover_host_1,
    failover_host_2,
    default_log_file_location,
    test_account_id, test_loadbalancer_id, test_vip_id, test_ipv6_vip_id, additional_vip_id, additional_ipv6_vip_id, zxtm_version
}
