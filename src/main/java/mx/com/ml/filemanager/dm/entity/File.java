package mx.com.ml.filemanager.dm.entity;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.UUID;

@Setter
@DynamoDbBean
public class File {

  private UUID id;

  @Getter
  private String name;

  @Getter
  private String contentType;

  @DynamoDbPartitionKey
  public UUID getId() {
    return id;
  }
}
