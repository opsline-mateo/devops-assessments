package com.myorg;

import software.amazon.awscdk.assertions.Template;
import software.amazon.awscdk.assertions.Match;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;

import java.util.Map;

public class S3BucketTest {

    private Template template;

    @BeforeEach
    public void setUp() {
        App app = new App();
        S3BucketStack stack = new S3BucketStack(app, "TestStack");
        template = Template.fromStack(stack);
    }

    @Test
    public void testS3BucketExists() {
        // Test that S3 bucket is created
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
            "BucketName", "my-bucket-name"
        )));
    }

    @Test
    public void testS3BucketHasVersioning() {
        // BUG: This test will fail because versioning is not enabled
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
            "VersioningConfiguration", Match.objectLike(Map.of(
                "Status", "Enabled"
            ))
        )));
    }

    @Test
    public void testS3BucketHasCORS() {
        // BUG: This test will fail because CORS is not configured
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
            "CorsConfiguration", Match.objectLike(Map.of(
                "CorsRules", Match.arrayWith(Map.of(
                    "AllowedMethods", Match.arrayWith("GET", "POST", "PUT"),
                    "AllowedOrigins", Match.arrayWith("*"),
                    "AllowedHeaders", Match.arrayWith("*")
                ))
            ))
        )));
    }

    @Test
    public void testS3BucketHasLifecycleRules() {
        // BUG: This test will fail because lifecycle rules are missing
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
            "LifecycleConfiguration", Match.objectLike(Map.of(
                "Rules", Match.arrayWith(Map.of(
                    "Status", "Enabled",
                    "Transitions", Match.arrayWith(Map.of(
                        "StorageClass", "STANDARD_IA",
                        "TransitionInDays", 30
                    ))
                ))
            ))
        )));
    }

    @Test
    public void testS3BucketHasEncryption() {
        // BUG: This test will fail because encryption is not configured
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
            "BucketEncryption", Match.objectLike(Map.of(
                "ServerSideEncryptionConfiguration", Match.arrayWith(Map.of(
                    "ServerSideEncryptionByDefault", Match.objectLike(Map.of(
                        "SSEAlgorithm", "AES256"
                    ))
                ))
            ))
        )));
    }

    @Test
    public void testS3BucketHasPublicAccessBlock() {
        // BUG: This test will fail because public access block is not configured
        template.hasResourceProperties("AWS::S3::BucketPolicy", Match.objectLike(Map.of(
            "PolicyDocument", Match.objectLike(Map.of(
                "Statement", Match.arrayWith(Map.of(
                    "Effect", "Deny",
                    "Principal", "*",
                    "Action", "s3:*",
                    "Resource", Match.arrayWith(Match.stringLikeRegexp(".*"))
                ))
            ))
        )));
    }

    @Test
    public void testS3BucketHasEventNotifications() {
        // BUG: This test will fail because event notifications are missing
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
            "NotificationConfiguration", Match.objectLike(Map.of(
                "LambdaConfigurations", Match.arrayWith(Map.of(
                    "Event", "s3:ObjectCreated:*",
                    "Function", Match.anyValue()
                ))
            ))
        )));
    }

    @Test
    public void testLambdaFunctionExists() {
        // BUG: This test will fail because Lambda function is not created
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "FunctionName", Match.stringLikeRegexp(".*S3Processor.*")
        )));
    }

    @Test
    public void testLambdaHasS3Permission() {
        // BUG: This test will fail because S3 permission is missing
        template.hasResourceProperties("AWS::IAM::Policy", Match.objectLike(Map.of(
            "PolicyName", Match.stringLikeRegexp(".*S3.*")
        )));
    }

    @Test
    public void testS3BucketHasLogging() {
        // BUG: This test will fail because logging is not configured
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
            "LoggingConfiguration", Match.objectLike(Map.of(
                "DestinationBucketName", Match.anyValue(),
                "LogFilePrefix", "access-logs/"
            ))
        )));
    }

    @Test
    public void testS3BucketHasWebsiteConfiguration() {
        // BUG: This test will fail because website configuration is missing
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
            "WebsiteConfiguration", Match.objectLike(Map.of(
                "IndexDocument", "index.html",
                "ErrorDocument", "error.html"
            ))
        )));
    }

    @Test
    public void testS3BucketHasInventoryConfiguration() {
        // BUG: This test will fail because inventory configuration is missing
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
            "InventoryConfiguration", Match.objectLike(Map.of(
                "Id", "inventory-config",
                "IsEnabled", true,
                "IncludedObjectVersions", "All"
            ))
        )));
    }

    @Test
    public void testS3BucketHasMetricsConfiguration() {
        // BUG: This test will fail because metrics configuration is missing
        template.hasResourceProperties("AWS::S3::Bucket", Match.objectLike(Map.of(
            "MetricsConfiguration", Match.objectLike(Map.of(
                "Id", "metrics-config",
                "Status", "Enabled"
            ))
        )));
    }
}
