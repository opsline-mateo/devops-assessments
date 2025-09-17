# Exercise 5: WAF with CloudFront and S3

## Objective
Implement a Web Application Firewall (WAF) to protect a CloudFront distribution that serves content from an S3 bucket. This exercise tests understanding of:
- AWS WAF rule creation and management
- IP sets and rate-based rules
- Geo-blocking configurations
- SQL injection and XSS protection
- CloudFront integration with WAF
- Logging and monitoring setup

## Requirements
1. Create an S3 bucket for static content
2. Set up CloudFront distribution
3. Implement WAF with the following rules:
   - IP whitelist/blacklist
   - Rate limiting (100 requests per 5 minutes)
   - Geo-blocking for specific countries
   - SQL injection protection
   - XSS (Cross-site scripting) protection
   - Size constraint rules
4. Configure WAF logging to S3
5. Add custom response headers

## Files to Complete
- `WafCloudFrontStack.java` - Implement the WAF and CloudFront configuration
- `WafCloudFrontTest.java` - Tests will validate your implementation

## Testing with LocalStack
The tests use LocalStack to simulate AWS services locally. Run:
```bash
mvn test
```

## Hints
- Use managed rule groups for common attack patterns
- Remember to associate WAF ACL with CloudFront
- Configure proper rule priorities
- Enable WAF logging for debugging