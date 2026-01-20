package com.constructflow.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MandatoryFieldsMissingException extends RuntimeException {
    public MandatoryFieldsMissingException(String message) {
        super(message);
    }
}
