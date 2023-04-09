package com.rdm.advancedtotems.api;

import java.util.TreeMap;

// https://stackoverflow.com/questions/12967896/converting-integers-to-roman-numerals-java
public class NumeralConverter {
    private static final TreeMap<Integer, String> NUMERAL_MAP = new TreeMap<Integer, String>();

    static {
        NUMERAL_MAP.put(1000, "M");
        NUMERAL_MAP.put(900, "CM");
        NUMERAL_MAP.put(500, "D");
        NUMERAL_MAP.put(400, "CD");
        NUMERAL_MAP.put(100, "C");
        NUMERAL_MAP.put(90, "XC");
        NUMERAL_MAP.put(50, "L");
        NUMERAL_MAP.put(40, "XL");
        NUMERAL_MAP.put(10, "X");
        NUMERAL_MAP.put(9, "IX");
        NUMERAL_MAP.put(5, "V");
        NUMERAL_MAP.put(4, "IV");
        NUMERAL_MAP.put(1, "I");
    }

    public static String toRoman(int number) {
        int key =  NUMERAL_MAP.floorKey(number);
        if (number == key) {
            return NUMERAL_MAP.get(number);
        }
        return NUMERAL_MAP.get(key) + toRoman(number-key);
    }
}
