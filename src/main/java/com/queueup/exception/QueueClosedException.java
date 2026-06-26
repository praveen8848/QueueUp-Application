package com.queueup.exception;

public class QueueClosedException extends RuntimeException {
    public QueueClosedException(String code) {
        super("Queue is closed: " + code);
    }
}