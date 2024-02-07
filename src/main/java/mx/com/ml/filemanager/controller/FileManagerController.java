package mx.com.ml.filemanager.controller;

import lombok.RequiredArgsConstructor;
import mx.com.ml.filemanager.dto.FileDto;
import mx.com.ml.filemanager.service.FileManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/file-manager")
public class FileManagerController {

  private final FileManagerService service;

  @PostMapping
  public ResponseEntity<FileDto> upload(@RequestParam(name = "file") final MultipartFile file) {
    return service.upload(file)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }

  @GetMapping(path = "/files")
  public ResponseEntity<List<FileDto>> fetchAll() {
    return service.fetchAll()
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.badRequest().build());
  }
}
