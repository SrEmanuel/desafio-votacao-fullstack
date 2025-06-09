package dev.emanuelm.votacao.exceptions;

public class SessaoPersistenceError extends RuntimeException {

  public SessaoPersistenceError(String message) {
    super(message);
  }
}
