package com.songsincommon.songsincommon.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UniqueCodeGeneratorTest {
    @Test
    public void generateRandomCode() {
        int length = 8;
        String code = UniqueCodeGenerator.generateUniqueCode(length);

        System.out.println(code);

        assertNotNull(code);
    }

    @Test
    public void generateUniqueCode() {
        int length = 12;
        String code1 = UniqueCodeGenerator.generateUniqueCode(length);
        String code2 = UniqueCodeGenerator.generateUniqueCode(length);

        System.out.println(code1);
        System.out.println(code2);

        assertNotEquals(code1, code2);
    }
}