package com.nhnacademy.auth.utils;

public class RedisUtils {
    private RedisUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getTokenPrefix() {
        return "Token:";
    }
}
