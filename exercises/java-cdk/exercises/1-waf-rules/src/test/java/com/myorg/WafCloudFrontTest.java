package com.myorg;

import software.amazon.awscdk.assertions.Template;
import software.amazon.awscdk.assertions.Match;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;

import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WafCloudFrontTest {

    private Template template;
    private Stack stack;

    @BeforeEach
    public void setUp() {
        App app = new App();
        stack = new WafCloudFrontStack(app, "TestStack");
        template = Template.fromStack(stack);
    }

    @Test
    @DisplayName("Should create S3 bucket with proper security configuration")
    public void testS3BucketConfiguration() {
        // Verify S3 bucket exists with versioning
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
            "VersioningConfiguration", Map.of(
                "Status", "Enabled"
            ),
            "BucketEncryption", Map.of(
                "ServerSideEncryptionConfiguration", List.of(Map.of(
                    "ServerSideEncryptionByDefault", Map.of(
                        "SSEAlgorithm", "AES256"
                    )
                ))
            ),
            "PublicAccessBlockConfiguration", Map.of(
                "BlockPublicAcls", true,
                "BlockPublicPolicy", true,
                "IgnorePublicAcls", true,
                "RestrictPublicBuckets", true
            )
        )));
    }

    @Test
    @DisplayName("Should create CloudFront distribution with S3 origin")
    public void testCloudFrontDistribution() {
        // Verify CloudFront distribution exists
        template.hasResourceProperties("AWS::CloudFront::Distribution", Match.objectLike(Map.of(
            "DistributionConfig", Map.of(
                "Enabled", true,
                "HttpVersion", "http2",
                "DefaultCacheBehavior", Match.objectLike(Map.of(
                    "Compress", true,
                    "ViewerProtocolPolicy", "redirect-to-https"
                ))
            )
        )));
    }

    @Test
    @DisplayName("Should create WAF Web ACL with proper scope")
    public void testWafWebACL() {
        // Verify WAF Web ACL exists with CLOUDFRONT scope
        template.hasResourceProperties("AWS::WAFv2::WebACL", Match.objectLike(Map.of(
            "Scope", "CLOUDFRONT",
            "DefaultAction", Map.of("Allow", Map.of())
        )));
    }

    @Test
    @DisplayName("Should have IP set for whitelist/blacklist")
    public void testWafIPSet() {
        // Verify IP set exists
        template.hasResourceProperties("AWS::WAFv2::IPSet", Match.objectLike(Map.of(
            "Scope", "CLOUDFRONT",
            "IPAddressVersion", "IPV4"
        )));
    }

    @Test
    @DisplayName("Should have rate-based rule limiting to 100 requests per 5 minutes")
    public void testWafRateBasedRule() {
        // Verify rate-based rule in Web ACL
        template.hasResourceProperties("AWS::WAFv2::WebACL", Match.objectLike(Map.of(
            "Rules", Match.arrayWith(List.of(
                Match.objectLike(Map.of(
                    "Name", Match.stringLikeRegexp(".*Rate.*"),
                    "Statement", Map.of(
                        "RateBasedStatement", Map.of(
                            "Limit", 100,
                            "AggregateKeyType", "IP"
                        )
                    ),
                    "Action", Map.of("Block", Map.of())
                ))
            ))
        )));
    }

    @Test
    @DisplayName("Should have geo-blocking rule")
    public void testWafGeoBlockingRule() {
        // Verify geo-blocking rule exists
        template.hasResourceProperties("AWS::WAFv2::WebACL", Match.objectLike(Map.of(
            "Rules", Match.arrayWith(List.of(
                Match.objectLike(Map.of(
                    "Name", Match.stringLikeRegexp(".*Geo.*"),
                    "Statement", Map.of(
                        "GeoMatchStatement", Map.of(
                            "CountryCodes", Match.anyValue()
                        )
                    ),
                    "Action", Map.of("Block", Map.of())
                ))
            ))
        )));
    }

    @Test
    @DisplayName("Should have SQL injection protection")
    public void testWafSQLInjectionProtection() {
        // Verify SQL injection protection using managed rules
        template.hasResourceProperties("AWS::WAFv2::WebACL", Match.objectLike(Map.of(
            "Rules", Match.arrayWith(List.of(
                Match.objectLike(Map.of(
                    "Name", Match.stringLikeRegexp(".*SQL.*"),
                    "Statement", Map.of(
                        "ManagedRuleGroupStatement", Map.of(
                            "VendorName", "AWS",
                            "Name", "AWSManagedRulesSQLiRuleSet"
                        )
                    ),
                    "OverrideAction", Map.of("None", Map.of())
                ))
            ))
        )));
    }

    @Test
    @DisplayName("Should have XSS protection")
    public void testWafXSSProtection() {
        // Verify XSS protection using managed rules
        template.hasResourceProperties("AWS::WAFv2::WebACL", Match.objectLike(Map.of(
            "Rules", Match.arrayWith(List.of(
                Match.objectLike(Map.of(
                    "Name", Match.stringLikeRegexp(".*XSS.*"),
                    "Statement", Map.of(
                        "ManagedRuleGroupStatement", Map.of(
                            "VendorName", "AWS",
                            "Name", "AWSManagedRulesKnownBadInputsRuleSet"
                        )
                    ),
                    "OverrideAction", Map.of("None", Map.of())
                ))
            ))
        )));
    }

    @Test
    @DisplayName("Should have size constraint rules")
    public void testWafSizeConstraints() {
        // Verify size constraint rules
        template.hasResourceProperties("AWS::WAFv2::WebACL", Match.objectLike(Map.of(
            "Rules", Match.arrayWith(List.of(
                Match.objectLike(Map.of(
                    "Name", Match.stringLikeRegexp(".*Size.*"),
                    "Statement", Map.of(
                        "SizeConstraintStatement", Map.of(
                            "ComparisonOperator", "GT",
                            "Size", Match.anyValue(),
                            "FieldToMatch", Match.anyValue()
                        )
                    ),
                    "Action", Map.of("Block", Map.of())
                ))
            ))
        )));
    }

    @Test
    @DisplayName("Should associate WAF with CloudFront distribution")
    public void testWafCloudFrontAssociation() {
        // Verify WAF is associated with CloudFront
        template.hasResourceProperties("AWS::CloudFront::Distribution", Match.objectLike(Map.of(
            "DistributionConfig", Map.of(
                "WebACLId", Match.anyValue()
            )
        )));
    }

    @Test
    @DisplayName("Should have S3 bucket for WAF logs")
    public void testWafLogBucket() {
        // Verify logging bucket exists
        Map<String, Object> buckets = template.findResources("AWS::S3::Bucket");
        assertTrue(buckets.size() >= 2, "Should have at least 2 S3 buckets (content and logs)");

        boolean hasLogBucket = buckets.values().stream()
            .anyMatch(bucket -> {
                Map<String, Object> properties = (Map<String, Object>) ((Map<String, Object>) bucket).get("Properties");
                String bucketName = (String) properties.get("BucketName");
                return bucketName != null && bucketName.contains("log");
            });

        assertTrue(hasLogBucket, "Should have a bucket for WAF logs");
    }

    @Test
    @DisplayName("Should configure WAF logging")
    public void testWafLoggingConfiguration() {
        // Verify WAF logging configuration
        template.hasResourceProperties("AWS::WAFv2::LoggingConfiguration", Match.objectLike(Map.of(
            "ResourceArn", Match.anyValue(),
            "LogDestinationConfigs", Match.anyValue()
        )));
    }

    @Test
    @DisplayName("Should have proper rule priorities")
    public void testWafRulePriorities() {
        // Verify rules have proper priorities
        template.hasResourceProperties("AWS::WAFv2::WebACL", Match.objectLike(Map.of(
            "Rules", Match.arrayWith(List.of(
                Match.objectLike(Map.of(
                    "Priority", 1
                )),
                Match.objectLike(Map.of(
                    "Priority", 2
                )),
                Match.objectLike(Map.of(
                    "Priority", 3
                ))
            ))
        )));
    }

    @Test
    @DisplayName("Should have custom response headers in CloudFront")
    public void testCloudFrontCustomHeaders() {
        // Verify custom response headers
        template.hasResourceProperties("AWS::CloudFront::Distribution", Match.objectLike(Map.of(
            "DistributionConfig", Map.of(
                "DefaultCacheBehavior", Match.objectLike(Map.of(
                    "ResponseHeadersPolicyId", Match.anyValue()
                ))
            )
        )));
    }
}