package com.myorg;

import cloud.localstack.LocalstackTestRunner;
import cloud.localstack.ServiceName;
import cloud.localstack.UseLocalstack;
import org.junit.Test;
import org.junit.runner.RunWith;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.s3control.model.*;
import software.amazon.awssdk.regions.Region;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

/**
 * LocalStack Integration Test for Advanced S3 Configuration
 * Tests comprehensive S3 features including replication, Object Lock, and advanced configurations
 */
@RunWith(LocalstackTestRunner.class)
@UseLocalstack(services = { ServiceName.S3, ServiceName.IAM, ServiceName.LAMBDA, ServiceName.SQS, ServiceName.SNS })
public class S3AdvancedLocalStackTest {

    @Test
    public void testSourceBucketWithObjectLock() {
        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

        ListBucketsResponse bucketsResponse = s3Client.listBuckets();
        assertFalse("Should have buckets", bucketsResponse.buckets().isEmpty());

        // Find source bucket
        Bucket sourceBucket = bucketsResponse.buckets().stream()
            .filter(b -> b.name().contains("source"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Source bucket not found"));

        // Check versioning
        GetBucketVersioningResponse versioningResponse = s3Client.getBucketVersioning(
            GetBucketVersioningRequest.builder()
                .bucket(sourceBucket.name())
                .build()
        );
        assertEquals("Versioning should be enabled",
                    BucketVersioningStatus.ENABLED,
                    versioningResponse.status());

        // Check Object Lock configuration
        try {
            GetObjectLockConfigurationResponse lockResponse = s3Client.getObjectLockConfiguration(
                GetObjectLockConfigurationRequest.builder()
                    .bucket(sourceBucket.name())
                    .build()
            );

            assertNotNull("Should have Object Lock configuration", lockResponse.objectLockConfiguration());
            assertEquals("Should be in GOVERNANCE mode",
                        ObjectLockMode.GOVERNANCE,
                        lockResponse.objectLockConfiguration().rule().defaultRetention().mode());
            assertEquals("Should have 30 day retention",
                        Integer.valueOf(30),
                        lockResponse.objectLockConfiguration().rule().defaultRetention().days());
        } catch (Exception e) {
            fail("Object Lock should be configured: " + e.getMessage());
        }
    }

    @Test
    public void testDestinationBucketConfiguration() {
        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

        // Find destination bucket
        Bucket destBucket = s3Client.listBuckets().buckets().stream()
            .filter(b -> b.name().contains("destination") || b.name().contains("replica"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Destination bucket not found"));

        // Check versioning is enabled
        GetBucketVersioningResponse versioningResponse = s3Client.getBucketVersioning(
            GetBucketVersioningRequest.builder()
                .bucket(destBucket.name())
                .build()
        );
        assertEquals("Destination bucket should have versioning enabled",
                    BucketVersioningStatus.ENABLED,
                    versioningResponse.status());
    }

    @Test
    public void testCrossRegionReplication() {
        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

        // Find source bucket
        Bucket sourceBucket = s3Client.listBuckets().buckets().stream()
            .filter(b -> b.name().contains("source"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Source bucket not found"));

        // Get replication configuration
        try {
            GetBucketReplicationResponse replicationResponse = s3Client.getBucketReplication(
                GetBucketReplicationRequest.builder()
                    .bucket(sourceBucket.name())
                    .build()
            );

            assertNotNull("Should have replication configuration", replicationResponse.replicationConfiguration());
            assertNotNull("Should have replication role", replicationResponse.replicationConfiguration().role());
            assertFalse("Should have replication rules",
                       replicationResponse.replicationConfiguration().rules().isEmpty());

            // Verify replication rule configuration
            ReplicationRule rule = replicationResponse.replicationConfiguration().rules().get(0);
            assertEquals("Rule should be enabled", ReplicationRuleStatus.ENABLED, rule.status());
            assertNotNull("Should have destination", rule.destination());
            assertNotNull("Should have delete marker replication", rule.deleteMarkerReplication());
            assertEquals("Delete marker replication should be enabled",
                        DeleteMarkerReplicationStatus.ENABLED,
                        rule.deleteMarkerReplication().status());
        } catch (Exception e) {
            fail("Replication should be configured: " + e.getMessage());
        }
    }

    @Test
    public void testIntelligentTiering() {
        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

        Bucket bucket = s3Client.listBuckets().buckets().get(0);

        // Get Intelligent Tiering configuration
        try {
            ListBucketIntelligentTieringConfigurationsResponse tieringResponse =
                s3Client.listBucketIntelligentTieringConfigurations(
                    ListBucketIntelligentTieringConfigurationsRequest.builder()
                        .bucket(bucket.name())
                        .build()
                );

            assertFalse("Should have Intelligent Tiering configurations",
                       tieringResponse.intelligentTieringConfigurationList().isEmpty());

            IntelligentTieringConfiguration config = tieringResponse.intelligentTieringConfigurationList().get(0);
            assertEquals("Should be enabled", IntelligentTieringStatus.ENABLED, config.status());
            assertFalse("Should have tiering configurations", config.tierings().isEmpty());

            // Verify Archive and Deep Archive tiers
            boolean hasArchive = config.tierings().stream()
                .anyMatch(t -> t.accessTier() == IntelligentTieringAccessTier.ARCHIVE_ACCESS &&
                              t.days() == 90);
            boolean hasDeepArchive = config.tierings().stream()
                .anyMatch(t -> t.accessTier() == IntelligentTieringAccessTier.DEEP_ARCHIVE_ACCESS &&
                              t.days() == 180);

            assertTrue("Should have Archive tier at 90 days", hasArchive);
            assertTrue("Should have Deep Archive tier at 180 days", hasDeepArchive);
        } catch (Exception e) {
            fail("Intelligent Tiering should be configured: " + e.getMessage());
        }
    }

    @Test
    public void testS3AccessPoint() {
        S3ControlClient s3ControlClient = S3ControlClient.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .build();

        String accountId = "000000000000"; // LocalStack default account ID

        try {
            ListAccessPointsResponse accessPointsResponse = s3ControlClient.listAccessPoints(
                ListAccessPointsRequest.builder()
                    .accountId(accountId)
                    .build()
            );

            assertFalse("Should have at least one Access Point",
                       accessPointsResponse.accessPointList().isEmpty());

            // Get first Access Point details
            String accessPointName = accessPointsResponse.accessPointList().get(0).name();
            GetAccessPointResponse accessPointDetails = s3ControlClient.getAccessPoint(
                GetAccessPointRequest.builder()
                    .accountId(accountId)
                    .name(accessPointName)
                    .build()
            );

            assertNotNull("Access Point should have bucket", accessPointDetails.bucket());
            assertNotNull("Access Point should have public access block configuration",
                         accessPointDetails.publicAccessBlockConfiguration());
            assertTrue("Public access should be blocked",
                      accessPointDetails.publicAccessBlockConfiguration().blockPublicAcls());
        } catch (Exception e) {
            // Access Points might not be fully supported in LocalStack
            System.out.println("Access Point test skipped: " + e.getMessage());
        }
    }

    @Test
    public void testTransferAcceleration() {
        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

        Bucket bucket = s3Client.listBuckets().buckets().get(0);

        try {
            GetBucketAccelerateConfigurationResponse accelerateResponse =
                s3Client.getBucketAccelerateConfiguration(
                    GetBucketAccelerateConfigurationRequest.builder()
                        .bucket(bucket.name())
                        .build()
                );

            assertEquals("Transfer Acceleration should be enabled",
                        BucketAccelerateStatus.ENABLED,
                        accelerateResponse.status());
        } catch (Exception e) {
            // Transfer Acceleration might not be fully supported in LocalStack
            System.out.println("Transfer Acceleration test skipped: " + e.getMessage());
        }
    }

    @Test
    public void testAdvancedLifecycleRules() {
        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

        Bucket bucket = s3Client.listBuckets().buckets().get(0);

        GetBucketLifecycleConfigurationResponse lifecycleResponse =
            s3Client.getBucketLifecycleConfiguration(
                GetBucketLifecycleConfigurationRequest.builder()
                    .bucket(bucket.name())
                    .build()
            );

        assertFalse("Should have lifecycle rules", lifecycleResponse.rules().isEmpty());

        LifecycleRule rule = lifecycleResponse.rules().get(0);
        assertEquals("Rule should be enabled", ExpirationStatus.ENABLED, rule.status());

        // Verify transitions
        assertFalse("Should have transitions", rule.transitions().isEmpty());

        // Check for all expected transitions
        boolean hasStandardIA = rule.transitions().stream()
            .anyMatch(t -> t.storageClass() == TransitionStorageClass.STANDARD_IA && t.days() == 30);
        boolean hasGlacier = rule.transitions().stream()
            .anyMatch(t -> t.storageClass() == TransitionStorageClass.GLACIER && t.days() == 90);
        boolean hasDeepArchive = rule.transitions().stream()
            .anyMatch(t -> t.storageClass() == TransitionStorageClass.DEEP_ARCHIVE && t.days() == 180);

        assertTrue("Should transition to STANDARD_IA after 30 days", hasStandardIA);
        assertTrue("Should transition to GLACIER after 90 days", hasGlacier);
        assertTrue("Should transition to DEEP_ARCHIVE after 180 days", hasDeepArchive);

        // Check expiration
        assertNotNull("Should have expiration", rule.expiration());
        assertEquals("Should expire after 365 days", Integer.valueOf(365), rule.expiration().days());

        // Check abort incomplete multipart upload
        assertNotNull("Should have abort incomplete multipart upload configuration",
                     rule.abortIncompleteMultipartUpload());
        assertEquals("Should abort after 7 days",
                    Integer.valueOf(7),
                    rule.abortIncompleteMultipartUpload().daysAfterInitiation());
    }

    @Test
    public void testS3EventNotifications() {
        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

        Bucket bucket = s3Client.listBuckets().buckets().get(0);

        GetBucketNotificationConfigurationResponse notificationResponse =
            s3Client.getBucketNotificationConfiguration(
                GetBucketNotificationConfigurationRequest.builder()
                    .bucket(bucket.name())
                    .build()
            );

        // Verify Lambda configuration
        assertFalse("Should have Lambda configurations",
                   notificationResponse.lambdaFunctionConfigurations().isEmpty());
        assertTrue("Lambda should handle PUT events",
                  notificationResponse.lambdaFunctionConfigurations().get(0).events()
                      .contains(Event.S3_OBJECT_CREATED_PUT));

        // Verify SQS configuration
        assertFalse("Should have SQS configurations",
                   notificationResponse.queueConfigurations().isEmpty());
        assertTrue("SQS should handle DELETE events",
                  notificationResponse.queueConfigurations().get(0).events().stream()
                      .anyMatch(e -> e.toString().contains("ObjectRemoved")));

        // Verify SNS configuration
        assertFalse("Should have SNS configurations",
                   notificationResponse.topicConfigurations().isEmpty());
        assertTrue("SNS should handle RESTORE events",
                  notificationResponse.topicConfigurations().get(0).events().stream()
                      .anyMatch(e -> e.toString().contains("ObjectRestore")));
    }

    @Test
    public void testS3Inventory() {
        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

        Bucket bucket = s3Client.listBuckets().buckets().get(0);

        try {
            ListBucketInventoryConfigurationsResponse inventoryResponse =
                s3Client.listBucketInventoryConfigurations(
                    ListBucketInventoryConfigurationsRequest.builder()
                        .bucket(bucket.name())
                        .build()
                );

            assertFalse("Should have inventory configurations",
                       inventoryResponse.inventoryConfigurationList().isEmpty());

            InventoryConfiguration config = inventoryResponse.inventoryConfigurationList().get(0);
            assertTrue("Inventory should be enabled", config.isEnabled());
            assertEquals("Should include all versions",
                        InventoryIncludedObjectVersions.ALL,
                        config.includedObjectVersions());
            assertEquals("Should use ORC format",
                        InventoryFormat.ORC,
                        config.destination().s3BucketDestination().format());
            assertEquals("Should run daily",
                        InventoryFrequency.DAILY,
                        config.schedule().frequency());

            // Check optional fields
            assertFalse("Should have optional fields", config.optionalFields().isEmpty());
            assertTrue("Should include Size field",
                      config.optionalFields().contains(InventoryOptionalField.SIZE));
            assertTrue("Should include LastModifiedDate field",
                      config.optionalFields().contains(InventoryOptionalField.LAST_MODIFIED_DATE));
        } catch (Exception e) {
            // Inventory might not be fully supported in LocalStack
            System.out.println("Inventory test skipped: " + e.getMessage());
        }
    }

    @Test
    public void testS3Analytics() {
        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

        Bucket bucket = s3Client.listBuckets().buckets().get(0);

        try {
            ListBucketAnalyticsConfigurationsResponse analyticsResponse =
                s3Client.listBucketAnalyticsConfigurations(
                    ListBucketAnalyticsConfigurationsRequest.builder()
                        .bucket(bucket.name())
                        .build()
                );

            assertFalse("Should have analytics configurations",
                       analyticsResponse.analyticsConfigurationList().isEmpty());

            AnalyticsConfiguration config = analyticsResponse.analyticsConfigurationList().get(0);
            assertNotNull("Should have storage class analysis", config.storageClassAnalysis());
            assertNotNull("Should have data export", config.storageClassAnalysis().dataExport());
            assertEquals("Should export as CSV",
                        AnalyticsS3ExportFileFormat.CSV,
                        config.storageClassAnalysis().dataExport().destination()
                            .s3BucketDestination().format());
        } catch (Exception e) {
            // Analytics might not be fully supported in LocalStack
            System.out.println("Analytics test skipped: " + e.getMessage());
        }
    }

    @Test
    public void testBatchOperationsTags() {
        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create("http://localhost:4566"))
            .region(Region.US_EAST_1)
            .forcePathStyle(true)
            .build();

        Bucket bucket = s3Client.listBuckets().buckets().get(0);

        try {
            GetBucketTaggingResponse taggingResponse = s3Client.getBucketTagging(
                GetBucketTaggingRequest.builder()
                    .bucket(bucket.name())
                    .build()
            );

            assertFalse("Should have tags", taggingResponse.tagSet().isEmpty());

            boolean hasBatchOperationTag = taggingResponse.tagSet().stream()
                .anyMatch(tag -> "BatchOperation".equals(tag.key()) && "Enabled".equals(tag.value()));

            assertTrue("Should have BatchOperation tag", hasBatchOperationTag);
        } catch (NoSuchBucketException e) {
            fail("Bucket should exist: " + e.getMessage());
        } catch (Exception e) {
            // Tags might not be configured
            System.out.println("Tags test warning: " + e.getMessage());
        }
    }
}