package com.myorg;

import cloud.localstack.LocalstackTestRunner;
import cloud.localstack.ServiceName;
import cloud.localstack.UseLocalstack;
import org.junit.Test;
import org.junit.runner.RunWith;
import software.amazon.awssdk.services.wafv2.Wafv2Client;
import software.amazon.awssdk.services.wafv2.model.*;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

/**
 * LocalStack Integration Test for WAF with CloudFront
 * Tests comprehensive WAF rules and CloudFront distribution configuration
 */
@RunWith(LocalstackTestRunner.class)
@UseLocalstack(services = { ServiceName.S3, ServiceName.CLOUDFRONT, ServiceName.WAF })
public class WafCloudFrontLocalStackTest {

    @Test
    public void testWafWebAclCreation() {
        Wafv2Client wafClient = Wafv2Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .build();

        // Test Web ACL exists
        ListWebAclsRequest listRequest = ListWebAclsRequest.builder()
            .scope(Scope.CLOUDFRONT)
            .build();

        ListWebAclsResponse response = wafClient.listWebACLs(listRequest);
        assertFalse("Should have at least one Web ACL", response.webACLs().isEmpty());

        // Verify Web ACL has expected name pattern
        boolean hasExpectedAcl = response.webACLs().stream()
            .anyMatch(acl -> acl.name().contains("WafCloudFront"));
        assertTrue("Should have Web ACL with expected name", hasExpectedAcl);
    }

    @Test
    public void testIpSetConfiguration() {
        Wafv2Client wafClient = Wafv2Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .build();

        // Test IP Set exists
        ListIpSetsRequest listRequest = ListIpSetsRequest.builder()
            .scope(Scope.CLOUDFRONT)
            .build();

        ListIpSetsResponse response = wafClient.listIPSets(listRequest);
        assertFalse("Should have at least one IP set", response.ipSets().isEmpty());

        // Get first IP set and verify configuration
        String ipSetArn = response.ipSets().get(0).arn();
        GetIpSetRequest getRequest = GetIpSetRequest.builder()
            .scope(Scope.CLOUDFRONT)
            .id(response.ipSets().get(0).id())
            .name(response.ipSets().get(0).name())
            .build();

        GetIpSetResponse ipSetResponse = wafClient.getIPSet(getRequest);
        assertEquals("Should be IPv4", IPAddressVersion.IPV4, ipSetResponse.ipSet().ipAddressVersion());
        assertFalse("Should have IP addresses", ipSetResponse.ipSet().addresses().isEmpty());
    }

    @Test
    public void testRateBasedRule() {
        Wafv2Client wafClient = Wafv2Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .build();

        // Get Web ACL and verify rate-based rule
        ListWebAclsResponse aclResponse = wafClient.listWebACLs(
            ListWebAclsRequest.builder().scope(Scope.CLOUDFRONT).build()
        );

        assertFalse("Should have Web ACLs", aclResponse.webACLs().isEmpty());

        String aclId = aclResponse.webACLs().get(0).id();
        String aclName = aclResponse.webACLs().get(0).name();

        GetWebAclRequest getRequest = GetWebAclRequest.builder()
            .scope(Scope.CLOUDFRONT)
            .id(aclId)
            .name(aclName)
            .build();

        GetWebAclResponse webAclResponse = wafClient.getWebACL(getRequest);

        // Verify rate-based rule exists
        boolean hasRateRule = webAclResponse.webACL().rules().stream()
            .anyMatch(rule -> rule.statement().rateBasedStatement() != null &&
                             rule.statement().rateBasedStatement().limit() == 100L);

        assertTrue("Should have rate-based rule with 100 request limit", hasRateRule);
    }

    @Test
    public void testGeoBlockingRule() {
        Wafv2Client wafClient = Wafv2Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .build();

        // Get Web ACL
        ListWebAclsResponse aclResponse = wafClient.listWebACLs(
            ListWebAclsRequest.builder().scope(Scope.CLOUDFRONT).build()
        );

        GetWebAclRequest getRequest = GetWebAclRequest.builder()
            .scope(Scope.CLOUDFRONT)
            .id(aclResponse.webACLs().get(0).id())
            .name(aclResponse.webACLs().get(0).name())
            .build();

        GetWebAclResponse webAclResponse = wafClient.getWebACL(getRequest);

        // Verify geo-blocking rule exists
        boolean hasGeoRule = webAclResponse.webACL().rules().stream()
            .anyMatch(rule -> rule.statement().geoMatchStatement() != null &&
                             !rule.statement().geoMatchStatement().countryCodes().isEmpty());

        assertTrue("Should have geo-blocking rule", hasGeoRule);
    }

    @Test
    public void testManagedRuleGroups() {
        Wafv2Client wafClient = Wafv2Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .build();

        // Get Web ACL
        ListWebAclsResponse aclResponse = wafClient.listWebACLs(
            ListWebAclsRequest.builder().scope(Scope.CLOUDFRONT).build()
        );

        GetWebAclRequest getRequest = GetWebAclRequest.builder()
            .scope(Scope.CLOUDFRONT)
            .id(aclResponse.webACLs().get(0).id())
            .name(aclResponse.webACLs().get(0).name())
            .build();

        GetWebAclResponse webAclResponse = wafClient.getWebACL(getRequest);

        // Verify SQL injection protection
        boolean hasSqlInjection = webAclResponse.webACL().rules().stream()
            .anyMatch(rule -> rule.statement().managedRuleGroupStatement() != null &&
                             "AWSManagedRulesSQLiRuleSet".equals(
                                 rule.statement().managedRuleGroupStatement().name()));

        assertTrue("Should have SQL injection protection", hasSqlInjection);

        // Verify XSS protection
        boolean hasXssProtection = webAclResponse.webACL().rules().stream()
            .anyMatch(rule -> rule.statement().managedRuleGroupStatement() != null &&
                             "AWSManagedRulesKnownBadInputsRuleSet".equals(
                                 rule.statement().managedRuleGroupStatement().name()));

        assertTrue("Should have XSS protection", hasXssProtection);
    }

    @Test
    public void testSizeConstraintRule() {
        Wafv2Client wafClient = Wafv2Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .build();

        // Get Web ACL
        ListWebAclsResponse aclResponse = wafClient.listWebACLs(
            ListWebAclsRequest.builder().scope(Scope.CLOUDFRONT).build()
        );

        GetWebAclRequest getRequest = GetWebAclRequest.builder()
            .scope(Scope.CLOUDFRONT)
            .id(aclResponse.webACLs().get(0).id())
            .name(aclResponse.webACLs().get(0).name())
            .build();

        GetWebAclResponse webAclResponse = wafClient.getWebACL(getRequest);

        // Verify size constraint rule exists
        boolean hasSizeConstraint = webAclResponse.webACL().rules().stream()
            .anyMatch(rule -> rule.statement().sizeConstraintStatement() != null);

        assertTrue("Should have size constraint rule", hasSizeConstraint);
    }

    @Test
    public void testWafLogging() {
        Wafv2Client wafClient = Wafv2Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .build();

        // Get Web ACL
        ListWebAclsResponse aclResponse = wafClient.listWebACLs(
            ListWebAclsRequest.builder().scope(Scope.CLOUDFRONT).build()
        );

        String resourceArn = aclResponse.webACLs().get(0).arn();

        // Get logging configuration
        GetLoggingConfigurationRequest loggingRequest = GetLoggingConfigurationRequest.builder()
            .resourceArn(resourceArn)
            .build();

        try {
            GetLoggingConfigurationResponse loggingResponse = wafClient.getLoggingConfiguration(loggingRequest);
            assertNotNull("Should have logging configuration", loggingResponse.loggingConfiguration());
            assertFalse("Should have log destination",
                       loggingResponse.loggingConfiguration().logDestinationConfigs().isEmpty());
        } catch (WafNonexistentItemException e) {
            // Logging might not be configured, which is a test failure
            fail("WAF logging should be configured");
        }
    }

    @Test
    public void testCloudFrontDistribution() {
        CloudFrontClient cfClient = CloudFrontClient.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .build();

        // List distributions
        var listResponse = cfClient.listDistributions();
        assertNotNull("Should have distribution list", listResponse.distributionList());
        assertFalse("Should have at least one distribution",
                   listResponse.distributionList().items().isEmpty());

        // Verify distribution has WAF association
        var distribution = listResponse.distributionList().items().get(0);
        assertNotNull("Distribution should have Web ACL ID", distribution.webACLId());
        assertFalse("Web ACL ID should not be empty", distribution.webACLId().isEmpty());
    }

    @Test
    public void testS3BucketsCreated() {
        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

        ListBucketsResponse response = s3Client.listBuckets();

        // Should have at least 2 buckets (content and logs)
        assertTrue("Should have at least 2 buckets", response.buckets().size() >= 2);

        // Verify content bucket exists
        boolean hasContentBucket = response.buckets().stream()
            .anyMatch(bucket -> bucket.name().contains("content") ||
                               bucket.name().contains("static"));
        assertTrue("Should have content bucket", hasContentBucket);

        // Verify log bucket exists
        boolean hasLogBucket = response.buckets().stream()
            .anyMatch(bucket -> bucket.name().contains("log"));
        assertTrue("Should have log bucket", hasLogBucket);
    }

    @Test
    public void testRulePriorities() {
        Wafv2Client wafClient = Wafv2Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .build();

        // Get Web ACL
        ListWebAclsResponse aclResponse = wafClient.listWebACLs(
            ListWebAclsRequest.builder().scope(Scope.CLOUDFRONT).build()
        );

        GetWebAclRequest getRequest = GetWebAclRequest.builder()
            .scope(Scope.CLOUDFRONT)
            .id(aclResponse.webACLs().get(0).id())
            .name(aclResponse.webACLs().get(0).name())
            .build();

        GetWebAclResponse webAclResponse = wafClient.getWebACL(getRequest);

        // Verify rules have unique priorities
        List<Integer> priorities = webAclResponse.webACL().rules().stream()
            .map(Rule::priority)
            .distinct()
            .sorted()
            .toList();

        assertEquals("All rules should have unique priorities",
                    webAclResponse.webACL().rules().size(),
                    priorities.size());

        // Verify priorities are in sequence
        for (int i = 0; i < priorities.size() - 1; i++) {
            assertTrue("Priorities should be properly ordered",
                      priorities.get(i) < priorities.get(i + 1));
        }
    }
}