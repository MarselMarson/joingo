package rt.marson.syeta.exception.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rt.marson.syeta.exception.DataValidationException;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException exception, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURL().toString(), HttpStatus.NOT_FOUND, "EntityNotFoundException", exception.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException exception, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURL().toString(), HttpStatus.BAD_REQUEST, "DataValidationException", exception.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadCredentialsException(BadCredentialsException exception, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURL().toString(), HttpStatus.BAD_REQUEST, "BadCredentialsException", exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNullPointerException(NullPointerException exception, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURL().toString(), HttpStatus.INTERNAL_SERVER_ERROR, "NullPointerException", exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURL().toString(), HttpStatus.FORBIDDEN, "AccessDeniedException", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException exception, HttpServletRequest request) {
        return new ErrorResponse(request.getRequestURL().toString(), HttpStatus.INTERNAL_SERVER_ERROR, "RuntimeException", exception.getMessage());
    }
}
