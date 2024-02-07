package mx.com.ml.filemanager.mapper;

import mx.com.ml.filemanager.dm.entity.File;
import mx.com.ml.filemanager.dto.FileDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {UUID.class}
)
public interface FileManagerMapper {

  FileDto toDto(final File source, final String url);

  @Mapping(target = "id", expression = "java(UUID.randomUUID())")
  File toEntity(final String name, final String contentType);
}
