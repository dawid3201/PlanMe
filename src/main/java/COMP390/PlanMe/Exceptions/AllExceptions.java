package COMP390.PlanMe.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class AllExceptions {
    //------------------------------------------------EXCEPTION-HANDLERS------------------------------------------------
    @ExceptionHandler
    public ResponseEntity<ExceptionsHandler> handleConflictException(ConflictException exc){
        ExceptionsHandler error = new ExceptionsHandler();

        error.setStatus(HttpStatus.CONFLICT.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    @ExceptionHandler
    public ResponseEntity<ExceptionsHandler> handleNotFoundException(NotFoundException exc){
        ExceptionsHandler error = new ExceptionsHandler();

        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<ExceptionsHandler> handleBadArgumentException(BadArgumentException exc){
        ExceptionsHandler error = new ExceptionsHandler();

        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setMessage(exc.getMessage());
        error.setTimeStamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}
