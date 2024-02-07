package mx.com.ml.filemanager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDto {

  private String name;

  private String contentType;

  private String url;
}
