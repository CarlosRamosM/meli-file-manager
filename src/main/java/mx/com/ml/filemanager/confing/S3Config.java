package mx.com.ml.filemanager.confing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.time.Duration;

@Configuration
public class S3Config {

  @Value("${amazon.aws.accesskey}")
  private String amazonAWSAccessKey;

  @Value("${amazon.aws.secretkey}")
  private String amazonAWSSecretKey;

  @Value("${amazon.aws.region}")
  private String region;

  @Bean
  public S3Client amazonS3() {
    AwsCredentialsProvider awsCredentialsProvider = this::getAwsCredentials;
    return S3Client
        .builder()
        .region(Region.of(region))
        .credentialsProvider(awsCredentialsProvider)
        .overrideConfiguration(overrideConfiguration())
        .build();
  }

  @Bean
  public S3Presigner s3Presigner() {
    return S3Presigner
        .builder()
        .credentialsProvider(this::getAwsCredentials)
        .region(Region.of(region))
        .build();
  }

  private AwsBasicCredentials getAwsCredentials() {
    return AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey);
  }

  private ClientOverrideConfiguration overrideConfiguration() {
    return ClientOverrideConfiguration
        .builder()
        .apiCallAttemptTimeout(Duration.ofSeconds(20))
        .retryPolicy(r -> r.numRetries(3))
        .build();
  }
}
