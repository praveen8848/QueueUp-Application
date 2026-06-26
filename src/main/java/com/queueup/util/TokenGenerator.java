package com.queueup.util;

import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {

    // Letters to use for tokens (A-Z except I and O)
    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int NUMBERS_PER_LETTER = 99;

    public String generate(long totalTicketsInQueue) {
        int letterIndex = (int) ((totalTicketsInQueue / NUMBERS_PER_LETTER) % LETTERS.length());
        int number = (int) ((totalTicketsInQueue % NUMBERS_PER_LETTER) + 1);

        return LETTERS.charAt(letterIndex) + String.valueOf(number);
    }
}