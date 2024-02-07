package mx.com.ml.filemanager.service;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.ml.filemanager.dm.entity.File;
import mx.com.ml.filemanager.dto.FileDto;
import mx.com.ml.filemanager.exception.FileManagerException;
import mx.com.ml.filemanager.integration.S3Service;
import mx.com.ml.filemanager.mapper.FileManagerMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileManagerService {

  private final S3Service s3Service;

  private final FileManagerMapper mapper;

  private final DynamoDbTemplate repository;

  public Optional<FileDto> upload(final MultipartFile file) {
    log.info("Upload ans Save file in AWS Services");
    var fileName = file.getOriginalFilename();
    var contentType = file.getContentType();
    var entity = mapper.toEntity(fileName, contentType);
    uploadFile(file);
    repository.save(entity);
    return Optional.of(mapper.toDto(entity, s3Service.generatePresignedUrl(fileName)));
  }

  public Optional<List<FileDto>> fetchAll() {
    var pageIterable = repository.scanAll(File.class);
    log.info("Items: {}", pageIterable.items().stream().count());
    return Optional.of(pageIterable
        .items()
        .stream()
        .map(file -> mapper.toDto(file, s3Service.generatePresignedUrl(file.getName())))
        .toList()
    );
  }

  private void uploadFile(final MultipartFile file) throws FileManagerException {
    try {
      s3Service.uploadFile(file.getOriginalFilename(), file.getContentType(), file.getBytes());
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new FileManagerException(e.getMessage(), e);
    }
  }
}
