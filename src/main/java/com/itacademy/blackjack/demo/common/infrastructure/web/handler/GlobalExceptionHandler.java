package cat.itacademy.blackjack.common.infrastructure.web.handler;

import cat.itacademy.blackjack.common.domain.exception.BlackjackException;
import cat.itacademy.blackjack.common.domain.exception.DomainException;
import cat.itacademy.blackjack.game_statistics.application.exception.EntityConflictException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Business Rule Error");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ResponseBody
    @ExceptionHandler(EntityConflictException.class)
    public ProblemDetail handleEntityConflictException(EntityConflictException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Conflict Error");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ResponseBody
    @ExceptionHandler(BlackjackException.class)
    public ProblemDetail handleBlackjackException(BlackjackException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Error");
        problemDetail.setDetail(ex.getMessage());
        return problemDetail;
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Error In Body Data");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Error in Parameter");
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> errors.put(
                violation.getPropertyPath().toString(), violation.getMessage()
        ));
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }
}

