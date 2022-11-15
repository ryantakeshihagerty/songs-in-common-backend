package com.songsincommon.songsincommon.util;

import java.util.Random;

public final class UniqueCodeGenerator {

    private final static Random random = new Random();
    private final static String alphanumericChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public static String generateUniqueCode(int length) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }

        StringBuilder uniqueCode = new StringBuilder();

        while (uniqueCode.length() < length) {
            int index = (int) (random.nextFloat() * alphanumericChars.length());
            uniqueCode.append(alphanumericChars.charAt(index));
        }

        return uniqueCode.toString();
    }
}
