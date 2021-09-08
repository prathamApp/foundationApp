package com.pratham.foundation.utility;

import java.security.SecureRandom;

public class FC_RandomString {

    // Maxim: Copied from UUID implementation :)
    private static volatile SecureRandom numberGenerator = null;
    private static final long MSB = 0x8000000000000000L;

    public static String unique() {
        SecureRandom ng = numberGenerator;
        if (ng == null) {
            numberGenerator = ng = new SecureRandom();
        }

        return Long.toHexString(MSB | ng.nextLong()) + Long.toHexString(MSB | ng.nextLong());
    }
/*
    */
/**
     * Generate a random string.
     *//*

    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String lower = upper.toLowerCase(Locale.ROOT);

    public static final String digits = "0123456789";

    public static final String alphanum = upper + lower + digits;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public FC_RandomString(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = alphanum.toCharArray();
        this.buf = new char[length];
    }

    */
/**
     * Create an alphanumeric string generator.
     *//*

    public FC_RandomString(int length, Random random) {
        this(length, random, alphanum);
    }

    */
/**
     * Create an alphanumeric strings from a secure generator.
     *//*

    public FC_RandomString(int length) {
        this(length, new SecureRandom());
    }

    */
/**
     * Create session identifiers.
     *//*

    public FC_RandomString() {
        this(21);
    }
*/

}