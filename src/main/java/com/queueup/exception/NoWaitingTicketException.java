package com.queueup.exception;

public class NoWaitingTicketException extends RuntimeException {
    public NoWaitingTicketException(String code) {
        super("No waiting tickets in queue: " + code);
    }
}