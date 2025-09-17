package com.myorg;

import software.amazon.awscdk.assertions.Template;
import software.amazon.awscdk.assertions.Match;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.sqs.Queue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;

import static org.assertj.core.api.Assertions.assertThat;

public class LambdaFunctionTest {

    private Template template;

    @BeforeEach
    public void setUp() {
        App app = new App();
        LambdaFunctionStack stack = new LambdaFunctionStack(app, "TestStack");
        template = Template.fromStack(stack);
    }

    @Test
    public void testLambdaFunctionExists() {
        // Test that Lambda function is created
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "FunctionName", "MyLambdaFunction"
        )));
    }

    @Test
    public void testLambdaRuntimeIsJava11() {
        // BUG: This test will fail because runtime is set to JAVA_8 instead of JAVA_11
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "Runtime", "java11"
        )));
    }

    @Test
    public void testLambdaHandlerIsCorrect() {
        // BUG: This test will fail because handler is wrong
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "Handler", "com.myorg.Handler::handleRequest"
        )));
    }

    @Test
    public void testLambdaHasEnvironmentVariables() {
        // BUG: This test will fail because environment variables are missing
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "Environment", Match.objectLike(Map.of(
                "Variables", Match.objectLike(Map.of(
                    "ENV", "dev",
                    "LOG_LEVEL", "INFO"
                ))
            ))
        )));
    }

    @Test
    public void testLambdaHasDeadLetterQueue() {
        // BUG: This test will fail because dead letter queue is missing
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "DeadLetterConfig", Match.objectLike(Map.of(
                "TargetArn", Match.anyValue()
            ))
        )));
    }

    @Test
    public void testDeadLetterQueueExists() {
        // BUG: This test will fail because dead letter queue is not created
        template.hasResourceProperties("AWS::SQS::Queue", Match.objectLike(Map.of(
            "QueueName", Match.stringLikeRegexp(".*dead-letter.*")
        )));
    }

    @Test
    public void testLambdaHasLogGroup() {
        // BUG: This test will fail because log group is missing
        template.hasResourceProperties("AWS::Logs::LogGroup", Match.objectLike(Map.of(
            "LogGroupName", Match.stringLikeRegexp(".*MyLambdaFunction.*")
        )));
    }

    @Test
    public void testLambdaTimeoutIsSet() {
        // BUG: This test will fail because timeout is not configured
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "Timeout", 30
        )));
    }

    @Test
    public void testLambdaMemoryIsSet() {
        // BUG: This test will fail because memory is not configured
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "MemorySize", 512
        )));
    }

    @Test
    public void testLambdaHasProperIAMRole() {
        // BUG: This test will fail because IAM role is missing
        template.hasResourceProperties("AWS::IAM::Role", Match.objectLike(Map.of(
            "RoleName", Match.stringLikeRegexp(".*MyLambdaFunction.*Role.*")
        )));
    }

    @Test
    public void testLambdaRoleHasCloudWatchLogsPolicy() {
        // BUG: This test will fail because CloudWatch Logs policy is missing
        template.hasResourceProperties("AWS::IAM::Policy", Match.objectLike(Map.of(
            "PolicyName", Match.stringLikeRegexp(".*CloudWatchLogs.*")
        )));
    }

    @Test
    public void testLambdaRoleHasSQSPolicy() {
        // BUG: This test will fail because SQS policy is missing
        template.hasResourceProperties("AWS::IAM::Policy", Match.objectLike(Map.of(
            "PolicyName", Match.stringLikeRegexp(".*SQS.*")
        )));
    }

    @Test
    public void testLambdaHasReservedConcurrency() {
        // BUG: This test will fail because reserved concurrency is not set
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "ReservedConcurrencyLimit", 10
        )));
    }

    @Test
    public void testLambdaHasTracingEnabled() {
        // BUG: This test will fail because X-Ray tracing is not enabled
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "TracingConfig", Match.objectLike(Map.of(
                "Mode", "Active"
            ))
        )));
    }
}
