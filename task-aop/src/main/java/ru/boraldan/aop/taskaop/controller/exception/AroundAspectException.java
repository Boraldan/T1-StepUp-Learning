package ru.boraldan.aop.taskaop.controller.exception;


public class AroundAspectException extends RuntimeException {

    public AroundAspectException(String message) {
        super(message);
    }
}
