package org.openstack.atlas.adapter.itest;

import org.junit.*;
import org.openstack.atlas.adapter.zxtm.ZxtmAdapterImpl;
import org.openstack.atlas.service.domain.entities.RateLimit;
import com.zxtm.service.client.VirtualServerBasicInfo;
import com.zxtm.service.client.VirtualServerProtocol;
import com.zxtm.service.client.VirtualServerRule;
import org.apache.axis.types.UnsignedInt;

import java.util.Calendar;

import static org.openstack.atlas.service.domain.entities.LoadBalancerProtocol.HTTPS;

@Ignore
public class RateLimitIntegrationTest extends ZeusTestBase {

    @BeforeClass
    public static void setupClass() throws InterruptedException {
        Thread.sleep(SLEEP_TIME_BETWEEN_TESTS);
        setupIvars();
        setupSimpleLoadBalancer();
    }

    @AfterClass
    public static void tearDownClass() {
        removeSimpleLoadBalancer();
    }

    @Test
    public void testSimpleRateLimitOperations() {
        //No longer a documented feature, the rate script needs to be installed to be useful
        setRateLimit();
        updateRateLimit();
        deleteRateLimit();
    }

    private void setRateLimit() {
        try {
            final Integer maxRequestsPerSecond = 1000;
            RateLimit rateLimit = new RateLimit();
            rateLimit.setExpirationTime(Calendar.getInstance());
            rateLimit.setMaxRequestsPerSecond(maxRequestsPerSecond);

            zxtmAdapter.setRateLimit(config, lb, rateLimit);

            String[] rateNames = getServiceStubs().getZxtmRateCatalogService().getRateNames();
            boolean doesExist = false;
            for (String rateName : rateNames) {
                if (rateName.equals(rateLimitName())) {
                    doesExist = true;
                    break;
                }
            }
            Assert.assertTrue(doesExist);

            final UnsignedInt[] ratePerSecondList = getServiceStubs().getZxtmRateCatalogService().getMaxRatePerSecond(new String[]{rateLimitName()});
            Assert.assertEquals(new UnsignedInt(maxRequestsPerSecond), ratePerSecondList[0]);

            final VirtualServerRule[][] virtualServerRules = getServiceStubs().getVirtualServerBinding().getRules(new String[]{loadBalancerName()});
            Assert.assertEquals(1, virtualServerRules.length);
            Assert.assertEquals(3, virtualServerRules[0].length);
            for (VirtualServerRule rule : virtualServerRules[0]) {
                if (!(rule.equals(ZxtmAdapterImpl.ruleRateLimitHttp)) && !(rule.equals(ZxtmAdapterImpl.ruleXForwardedProto)) && !(rule.equals(ZxtmAdapterImpl.ruleXForwardedFor))) {
                    Assert.fail("None of the rules matched, test failed!...");
                }
            }
//            Assert.assertEquals(ZxtmAdapterImpl.ruleRateLimitHttp, virtualServerRules[0][1]);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private void updateRateLimit() {
        try {
            final Integer maxRequestsPerSecond = 5;
            RateLimit rateLimit = new RateLimit();
            rateLimit.setExpirationTime(Calendar.getInstance());
            rateLimit.setMaxRequestsPerSecond(maxRequestsPerSecond);

            zxtmAdapter.updateRateLimit(config, lb, rateLimit);

            String[] rateNames = getServiceStubs().getZxtmRateCatalogService().getRateNames();
            boolean doesExist = false;
            for (String rateName : rateNames) {
                if (rateName.equals(rateLimitName())) {
                    doesExist = true;
                    break;
                }
            }
            Assert.assertTrue(doesExist);

            final UnsignedInt[] ratePerSecondList = getServiceStubs().getZxtmRateCatalogService().getMaxRatePerSecond(new String[]{rateLimitName()});
            Assert.assertEquals(new UnsignedInt(maxRequestsPerSecond), ratePerSecondList[0]);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private void deleteRateLimit() {
        try {
            zxtmAdapter.deleteRateLimit(config, lb);
            String[] rateNames = getServiceStubs().getZxtmRateCatalogService().getRateNames();
            boolean doesExist = false;
            for (String rateName : rateNames) {
                if (rateName.equals(rateLimitName())) {
                    doesExist = true;
                    break;
                }
            }
            Assert.assertFalse(doesExist);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRateLimitOperationsWhenChangingBetweenHttpAndNonHttpProtocols() {
        setRateLimit();
        updateProtocolToHttps();
        deleteRateLimit();
    }

    private void updateProtocolToHttps() {
        try {
            lb.setProtocol(HTTPS);
            zxtmAdapter.updateProtocol(config, lb);

            final VirtualServerBasicInfo[] virtualServerBasicInfos = getServiceStubs().getVirtualServerBinding().getBasicInfo(new String[]{loadBalancerName()});
            Assert.assertEquals(1, virtualServerBasicInfos.length);
            Assert.assertEquals(VirtualServerProtocol.https, virtualServerBasicInfos[0].getProtocol());

            final VirtualServerRule[][] virtualServerRules = getServiceStubs().getVirtualServerBinding().getRules(new String[]{loadBalancerName()});
            Assert.assertEquals(1, virtualServerRules.length);
            Assert.assertEquals(1, virtualServerRules[0].length);
            Assert.assertEquals(ZxtmAdapterImpl.ruleRateLimitNonHttp, virtualServerRules[0][0]);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}
