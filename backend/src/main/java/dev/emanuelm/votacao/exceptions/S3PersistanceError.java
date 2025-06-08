package dev.emanuelm.votacao.exceptions;

public class S3PersistanceError extends RuntimeException {
  public S3PersistanceError(String message) {
    super(message);
  }
}
