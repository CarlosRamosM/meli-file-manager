package mx.com.ml.filemanager.exception;

public class FileManagerException extends RuntimeException {

  public FileManagerException(String errorMessage) {
    super(errorMessage);
  }

  public FileManagerException(String errorMessage, Throwable e) {
    super(errorMessage, e);
  }
}
