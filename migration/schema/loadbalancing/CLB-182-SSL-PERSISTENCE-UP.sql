use `loadbalancing`;

INSERT INTO lb_session_persistence values('SSL_ID', 'Indicates that the load balancer uses SSL_ID session persistence',true);

UPDATE `meta` SET `meta_value` = '?' WHERE `meta_key`='version';