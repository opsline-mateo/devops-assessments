package com.myorg;

import software.amazon.awscdk.assertions.Template;
import software.amazon.awscdk.assertions.Match;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Stack;

import java.util.Map;

public class ApiGatewayTest {

    private Template template;

    @BeforeEach
    public void setUp() {
        App app = new App();
        ApiGatewayStack stack = new ApiGatewayStack(app, "TestStack");
        template = Template.fromStack(stack);
    }

    @Test
    public void testApiGatewayExists() {
        // BUG: This test will fail because API Gateway is not created
        template.hasResourceProperties("AWS::ApiGateway::RestApi", Match.objectLike(Map.of(
            "Name", "MyApi"
        )));
    }

    @Test
    public void testApiGatewayHasCORS() {
        // BUG: This test will fail because CORS is not configured
        template.hasResourceProperties("AWS::ApiGateway::Method", Match.objectLike(Map.of(
            "HttpMethod", "OPTIONS",
            "Integration", Match.objectLike(Map.of(
                "IntegrationResponses", Match.arrayWith(Map.of(
                    "ResponseParameters", Match.objectLike(Map.of(
                        "method.response.header.Access-Control-Allow-Headers", "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'",
                        "method.response.header.Access-Control-Allow-Origin", "'*'"
                    ))
                ))
            ))
        )));
    }

    @Test
    public void testApiGatewayHasLambdaIntegration() {
        // BUG: This test will fail because Lambda integration is missing
        template.hasResourceProperties("AWS::ApiGateway::Method", Match.objectLike(Map.of(
            "Integration", Match.objectLike(Map.of(
                "Type", "AWS_PROXY",
                "IntegrationHttpMethod", "POST"
            ))
        )));
    }

    @Test
    public void testApiGatewayHasApiKey() {
        // BUG: This test will fail because API key is not created
        template.hasResourceProperties("AWS::ApiGateway::ApiKey", Match.objectLike(Map.of(
            "Name", "MyApiKey"
        )));
    }

    @Test
    public void testApiGatewayHasUsagePlan() {
        // BUG: This test will fail because usage plan is missing
        template.hasResourceProperties("AWS::ApiGateway::UsagePlan", Match.objectLike(Map.of(
            "UsagePlanName", "MyUsagePlan"
        )));
    }

    @Test
    public void testApiGatewayHasDeployment() {
        // BUG: This test will fail because deployment is missing
        template.hasResourceProperties("AWS::ApiGateway::Deployment", Match.objectLike(Map.of(
            "StageName", "prod"
        )));
    }

    @Test
    public void testApiGatewayHasLogging() {
        // BUG: This test will fail because logging is not configured
        template.hasResourceProperties("AWS::ApiGateway::Stage", Match.objectLike(Map.of(
            "MethodSettings", Match.arrayWith(Map.of(
                "ResourcePath", "/*",
                "HttpMethod", "*",
                "LoggingLevel", "INFO"
            ))
        )));
    }

    @Test
    public void testApiGatewayHasThrottling() {
        // BUG: This test will fail because throttling is not configured
        template.hasResourceProperties("AWS::ApiGateway::UsagePlan", Match.objectLike(Map.of(
            "Throttle", Match.objectLike(Map.of(
                "BurstLimit", 100,
                "RateLimit", 50
            ))
        )));
    }

    @Test
    public void testApiGatewayHasRequestValidation() {
        // BUG: This test will fail because request validation is missing
        template.hasResourceProperties("AWS::ApiGateway::RequestValidator", Match.objectLike(Map.of(
            "Name", "MyRequestValidator"
        )));
    }

    @Test
    public void testApiGatewayHasResponseValidation() {
        // BUG: This test will fail because response validation is missing
        template.hasResourceProperties("AWS::ApiGateway::Method", Match.objectLike(Map.of(
            "MethodResponses", Match.arrayWith(Map.of(
                "StatusCode", "200",
                "ResponseParameters", Match.objectLike(Map.of(
                    "method.response.header.Content-Type", true
                ))
            ))
        )));
    }

    @Test
    public void testApiGatewayHasLambdaFunctions() {
        // BUG: This test will fail because Lambda functions are not created
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "FunctionName", Match.stringLikeRegexp(".*GetFunction.*")
        )));
        
        template.hasResourceProperties("AWS::Lambda::Function", Match.objectLike(Map.of(
            "FunctionName", Match.stringLikeRegexp(".*PostFunction.*")
        )));
    }

    @Test
    public void testApiGatewayHasLambdaPermissions() {
        // BUG: This test will fail because Lambda permissions are missing
        template.hasResourceProperties("AWS::Lambda::Permission", Match.objectLike(Map.of(
            "Action", "lambda:InvokeFunction"
        )));
    }

    @Test
    public void testApiGatewayHasCloudWatchRole() {
        // BUG: This test will fail because CloudWatch role is missing
        template.hasResourceProperties("AWS::IAM::Role", Match.objectLike(Map.of(
            "RoleName", Match.stringLikeRegexp(".*ApiGatewayCloudWatchRole.*")
        )));
    }

    @Test
    public void testApiGatewayHasResourcePolicies() {
        // BUG: This test will fail because resource policies are missing
        template.hasResourceProperties("AWS::ApiGateway::RestApi", Match.objectLike(Map.of(
            "Policy", Match.objectLike(Map.of(
                "Version", "2012-10-17",
                "Statement", Match.arrayWith(Map.of(
                    "Effect", "Allow",
                    "Principal", "*",
                    "Action", "execute-api:Invoke"
                ))
            ))
        )));
    }
}
