package com.vouched.error;

import com.vouched.model.dto.BasicErrorResponse;
import io.sentry.Sentry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {


    private Exception convertErrorToHumanReadable(Exception ex) {
        final String error = ex.getMessage();
        if (error.contains("duplicate key")) {
            return new SoftException("An entry already exists for this item");
        }
        return ex;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<BasicErrorResponse> handleException(Exception ex) {
        final Exception convertedException = convertErrorToHumanReadable(ex);
        if (convertedException instanceof SoftException) {
            return new ResponseEntity<>(new BasicErrorResponse(convertedException.getMessage()), HttpStatus.BAD_REQUEST);
        }
        // Capture as reportable error.
        Sentry.captureException(convertedException);
        return new ResponseEntity<>(new BasicErrorResponse(convertedException.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}