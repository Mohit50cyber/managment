package com.moglix.wms.exception;

public class InvalidUserTokenException extends Exception {
    public InvalidUserTokenException(String message) {
        super(message);
    }
}