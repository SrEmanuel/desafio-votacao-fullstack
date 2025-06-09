package dev.emanuelm.votacao.exceptions;

public class VotoPersistenceError extends RuntimeException {
  public VotoPersistenceError(String message) {
    super(message);
  }
}
