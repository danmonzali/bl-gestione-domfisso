package it.tim.bl.gestione.domfisso.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - errore di request o risposta 4xx da un servizio esterno
    @ExceptionHandler({
            HttpStatusCodeException.class,
            HttpMessageNotReadableException.class,
            MissingRequestHeaderException.class
    })
    public ResponseEntity<String> handleBadRequest(Exception ex) {
        return new ResponseEntity<>(
                new BRExceptionReturn(
                        BRExceptionReturn.CODE_103,
                        BRExceptionReturn.CODE_103_MSG).toString(),
                HttpStatus.BAD_REQUEST);
    }

    // 400 errori di validazione
    @ExceptionHandler(BRExceptionReturn.class)
    public ResponseEntity<String> handleBRExceptionReturn(BRExceptionReturn ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.toString());
    }

    // 500 - errore generico oppure operazione su database di GUP non riuscita
    @ExceptionHandler(ISEExceptionReturn.class)
    public ResponseEntity<String> handleISEExceptionReturn(ISEExceptionReturn ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.toString());
    }
}
