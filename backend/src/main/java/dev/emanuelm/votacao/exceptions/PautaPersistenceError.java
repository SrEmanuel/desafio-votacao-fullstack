package dev.emanuelm.votacao.exceptions;

public class PautaPersistenceError extends RuntimeException {

  public PautaPersistenceError(String message) {
    super(message);
  }
}
