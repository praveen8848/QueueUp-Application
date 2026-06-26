package com.queueup.exception;

public class QueueNotFoundException extends RuntimeException {
    public QueueNotFoundException(String code) {
        super("Queue not found with code: " + code);
    }
}