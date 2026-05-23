package com.example.student_management.api;

public class TokenManager {

    private static String token = null;

    public static void saveToken(String jwtToken) {
        token = jwtToken;
    }

    public static String getToken() {
        return token;
    }

    public static void clearToken() {
        token = null;
    }

    public static boolean hasToken() {
        return token != null && !token.isEmpty();
    }

    public static String getBearerToken() {
        return "Bearer " + token;
    }
}