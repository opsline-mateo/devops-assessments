package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

public class WafCloudFrontApp {
    public static void main(final String[] args) {
        App app = new App();

        new WafCloudFrontStack(app, "WafCloudFrontStack", StackProps.builder()
                .env(Environment.builder()
                        .account(System.getenv().getOrDefault("CDK_DEFAULT_ACCOUNT", "123456789012"))
                        .region(System.getenv().getOrDefault("CDK_DEFAULT_REGION", "us-east-1"))
                        .build())
                .build());

        app.synth();
    }
}