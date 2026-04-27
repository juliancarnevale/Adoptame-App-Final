package com.adoptame.utils;

import com.adoptame.models.User;

public class SessionManager {
    private static User currentUser;

    public static void setUser(User user) {
        currentUser = user;
    }

    public static User getUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}