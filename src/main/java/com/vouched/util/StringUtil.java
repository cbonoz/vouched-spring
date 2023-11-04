package com.vouched.util;

public class StringUtil {

    // isEmpty
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    // valid email
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

}