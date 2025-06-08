package dev.emanuelm.votacao.exceptions;

public class GenericServiceError extends RuntimeException{

  public GenericServiceError(String message) {
    super(message);
  }

  public GenericServiceError(String message, Throwable t) {
    super(message, t);
  }

}
