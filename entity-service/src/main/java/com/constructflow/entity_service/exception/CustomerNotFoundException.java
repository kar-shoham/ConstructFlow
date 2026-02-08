package com.constructflow.entity_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException
        extends RuntimeException
{

    public CustomerNotFoundException(String message)
    {
        super(message);
    }

    public CustomerNotFoundException(Long id)
    {
        super("No Customer with ID: " + id + " !");
    }
}
