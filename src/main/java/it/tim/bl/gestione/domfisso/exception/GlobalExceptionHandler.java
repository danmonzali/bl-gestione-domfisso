package it.tim.bl.gestione.domfisso.exception;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);
    private static final String GUP_EVENT_RETURN_CODE = "gupEventReturnCode";

    // 400 - errore di request o risposta 4xx da un servizio esterno
    @ExceptionHandler({
            HttpStatusCodeException.class,
            HttpMessageNotReadableException.class,
            MissingRequestHeaderException.class
    })
    public ResponseEntity<String> handleBadRequest(Exception ex) {
        ThreadContext.put(GUP_EVENT_RETURN_CODE, BRExceptionReturn.CODE_103);
        logger.warn("Richiesta non valida (code {}): {}", BRExceptionReturn.CODE_103, ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BRExceptionReturn(
                        BRExceptionReturn.CODE_103,
                        BRExceptionReturn.CODE_103_MSG).toString());
    }

    // 400 errori di validazione
    @ExceptionHandler(BRExceptionReturn.class)
    public ResponseEntity<String> handleBRExceptionReturn(BRExceptionReturn ex) {
        ThreadContext.put(GUP_EVENT_RETURN_CODE, ex.code);
        logger.warn("Richiesta non valida (code {})", ex.code);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.toString());
    }

    // 500 - errore generico oppure operazione su database di GUP non riuscita
    @ExceptionHandler(ISEExceptionReturn.class)
    public ResponseEntity<String> handleISEExceptionReturn(ISEExceptionReturn ex) {
        ThreadContext.put(GUP_EVENT_RETURN_CODE, ex.code);
        logger.error("Errore interno (code {})", ex.code);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.toString());
    }
}
