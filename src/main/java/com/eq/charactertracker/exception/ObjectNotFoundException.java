package com.eq.charactertracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObjectNotFoundException extends NoSuchElementException {
    public ObjectNotFoundException(Long characterId){
        super("Object not found with Id: " + characterId);
    }
}
