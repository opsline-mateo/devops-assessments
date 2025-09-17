package com.myorg;

import software.amazon.awscdk.assertions.Template;
import software.amazon.awscdk.assertions.Match;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;

import java.util.Map;

public class DynamoDbTableTest {

    private Template template;

    @BeforeEach
    public void setUp() {
        App app = new App();
        DynamoDbTableStack stack = new DynamoDbTableStack(app, "TestStack");
        template = Template.fromStack(stack);
    }

    @Test
    public void testDynamoDbTableExists() {
        // BUG: This test will fail because DynamoDB table is not created
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "TableName", "MyTable"
        )));
    }

    @Test
    public void testDynamoDbTableHasCorrectKeySchema() {
        // BUG: This test will fail because key schema is not configured
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "KeySchema", Match.arrayWith(
                Map.of("AttributeName", "id", "KeyType", "HASH"),
                Map.of("AttributeName", "timestamp", "KeyType", "RANGE")
            )
        )));
    }

    @Test
    public void testDynamoDbTableHasGlobalSecondaryIndexes() {
        // BUG: This test will fail because GSI are not configured
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "GlobalSecondaryIndexes", Match.arrayWith(Map.of(
                "IndexName", "GSI1",
                "KeySchema", Match.arrayWith(
                    Map.of("AttributeName", "status", "KeyType", "HASH"),
                    Map.of("AttributeName", "createdAt", "KeyType", "RANGE")
                )
            ))
        )));
    }

    @Test
    public void testDynamoDbTableHasAutoScaling() {
        // BUG: This test will fail because auto-scaling is not configured
        template.hasResourceProperties("AWS::ApplicationAutoScaling::ScalableTarget", Match.objectLike(Map.of(
            "ScalableDimension", "dynamodb:table:ReadCapacityUnits"
        )));
        
        template.hasResourceProperties("AWS::ApplicationAutoScaling::ScalableTarget", Match.objectLike(Map.of(
            "ScalableDimension", "dynamodb:table:WriteCapacityUnits"
        )));
    }

    @Test
    public void testDynamoDbTableHasPointInTimeRecovery() {
        // BUG: This test will fail because point-in-time recovery is not enabled
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "PointInTimeRecoverySpecification", Match.objectLike(Map.of(
                "PointInTimeRecoveryEnabled", true
            ))
        )));
    }

    @Test
    public void testDynamoDbTableHasBackupPolicy() {
        // BUG: This test will fail because backup policy is not configured
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "BackupPolicy", Match.objectLike(Map.of(
                "BackupPolicyName", "MyBackupPolicy"
            ))
        )));
    }

    @Test
    public void testDynamoDbTableHasEncryption() {
        // BUG: This test will fail because encryption is not configured
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "SSESpecification", Match.objectLike(Map.of(
                "SSEEnabled", true,
                "SSEType", "KMS"
            ))
        )));
    }

    @Test
    public void testDynamoDbTableHasTTL() {
        // BUG: This test will fail because TTL is not configured
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "TimeToLiveSpecification", Match.objectLike(Map.of(
                "AttributeName", "ttl",
                "Enabled", true
            ))
        )));
    }

    @Test
    public void testDynamoDbTableHasStream() {
        // BUG: This test will fail because stream is not configured
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "StreamSpecification", Match.objectLike(Map.of(
                "StreamViewType", "NEW_AND_OLD_IMAGES"
            ))
        )));
    }

    @Test
    public void testDynamoDbTableHasIAMRole() {
        // BUG: This test will fail because IAM role is not created
        template.hasResourceProperties("AWS::IAM::Role", Match.objectLike(Map.of(
            "RoleName", Match.stringLikeRegexp(".*DynamoDb.*Role.*")
        )));
    }

    @Test
    public void testDynamoDbTableHasIAMPolicy() {
        // BUG: This test will fail because IAM policy is not created
        template.hasResourceProperties("AWS::IAM::Policy", Match.objectLike(Map.of(
            "PolicyName", Match.stringLikeRegexp(".*DynamoDb.*Policy.*")
        )));
    }

    @Test
    public void testDynamoDbTableHasCorrectBillingMode() {
        // BUG: This test will fail because billing mode is not configured
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "BillingMode", "PAY_PER_REQUEST"
        )));
    }

    @Test
    public void testDynamoDbTableHasCorrectAttributeDefinitions() {
        // BUG: This test will fail because attribute definitions are missing
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "AttributeDefinitions", Match.arrayWith(
                Map.of("AttributeName", "id", "AttributeType", "S"),
                Map.of("AttributeName", "timestamp", "AttributeType", "N"),
                Map.of("AttributeName", "status", "AttributeType", "S"),
                Map.of("AttributeName", "createdAt", "AttributeType", "N")
            )
        )));
    }

    @Test
    public void testDynamoDbTableHasLocalSecondaryIndex() {
        // BUG: This test will fail because LSI is not configured
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "LocalSecondaryIndexes", Match.arrayWith(Map.of(
                "IndexName", "LSI1",
                "KeySchema", Match.arrayWith(
                    Map.of("AttributeName", "id", "KeyType", "HASH"),
                    Map.of("AttributeName", "category", "KeyType", "RANGE")
                )
            ))
        )));
    }

    @Test
    public void testDynamoDbTableHasTags() {
        // BUG: This test will fail because tags are not configured
        template.hasResourceProperties("AWS::DynamoDB::Table", Match.objectLike(Map.of(
            "Tags", Match.arrayWith(
                Map.of("Key", "Environment", "Value", "dev"),
                Map.of("Key", "Project", "Value", "MyProject")
            )
        )));
    }
}
