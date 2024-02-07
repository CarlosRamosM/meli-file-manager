package mx.com.ml.filemanager.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.ml.filemanager.exception.FileManagerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

  @Value("${amazon.s3.bucket}")
  private String bucket;

  private final S3Client s3Client;

  private final S3Presigner s3Presigner;

  public void uploadFile(final String s3Key, final String contentType, final byte[] file) throws FileManagerException {
    log.info("Subiendo imagen a AWS S3");
    log.info("S3 Key: {}", s3Key);
    if (objectExists(bucket, s3Key)) {
      throw new FileManagerException("vee.error.s3.key.already.exits");
    }
    uploadFileToS3Bucket(bucket, s3Key, contentType, file);
  }

  public String generatePresignedUrl(final String key) throws FileManagerException {
    log.info("Firma: {} : {}", bucket, key);
    try {
      var getObjectPresignRequest = GetObjectPresignRequest
          .builder()
          .signatureDuration(Duration.ofMinutes(10))
          .getObjectRequest(objectRequest -> objectRequest.bucket(bucket).key(key))
          .build();
      var presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
      final URL url = presignedGetObjectRequest.url();
      return url.toString();
    } catch (S3Exception e) {
      log.error(e.getMessage(), e);
      throw new FileManagerException(e.getMessage(), e);
    }
  }

  private void uploadFileToS3Bucket(final String bucketName,
                                    final String key,
                                    final String contentType,
                                    final byte[] file) throws FileManagerException {
    log.info("Upload object to S3");
    try {
      s3Client.putObject(
          PutObjectRequest
              .builder()
              .bucket(bucketName)
              .key(key)
              .contentType(contentType)
              .build(),
          RequestBody
              .fromBytes(file)
      );
    } catch (S3Exception e) {
      log.error(e.getMessage(), e);
      throw new FileManagerException(e.getMessage(), e);
    }
  }

  private boolean objectExists(final String bucketName, final String folderName) throws FileManagerException {
    try {
      var request = ListObjectsRequest
          .builder()
          .bucket(bucketName)
          .build();
      var response = s3Client.listObjects(request);
      var objects = response.contents();
      objects.forEach(o -> log.info("Name o Key: {}", o.key()));
      return objects
          .stream()
          .anyMatch(o -> folderName.equalsIgnoreCase(o.key()));
    } catch (S3Exception e) {
      log.error(e.getMessage(), e);
      throw new FileManagerException(e.getMessage(), e);
    }
  }
}
