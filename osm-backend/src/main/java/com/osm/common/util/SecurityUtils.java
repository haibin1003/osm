package com.osm.common.util;

public class SecurityUtils {

    public static String getCurrentUserId() {
        // TODO: integrate with real authentication
        return "test-user";
    }

    public static boolean isAdmin() {
        // TODO: integrate with real authentication
        return true;
    }
}
