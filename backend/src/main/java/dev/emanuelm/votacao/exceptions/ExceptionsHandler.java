package dev.emanuelm.votacao.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionsHandler {

  @ExceptionHandler({GenericServiceError.class})
  public ResponseEntity<ErroDTO> handleException(GenericServiceError error, WebRequest request) {
    ErroDTO erro = new ErroDTO("SERVICE_ERROR", error.getMessage());
    return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({PautaPersistenceError.class})
  public ResponseEntity<ErroDTO> handleException(PautaPersistenceError error, WebRequest request) {
    ErroDTO erro = new ErroDTO("PAUTA_ERROR", error.getMessage());
    return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({SessaoPersistenceError.class})
  public ResponseEntity<ErroDTO> handleException(SessaoPersistenceError error, WebRequest request) {
    ErroDTO erro = new ErroDTO("SESSAO_VOTACAO_ERROR", error.getMessage());
    return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
  }


}
