package com.constructflow.entity_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TimesheetValidationException
        extends RuntimeException
{
    public TimesheetValidationException(String message)
    {
        super(message);
    }
}
