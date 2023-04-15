package com.cosmos.common.security.util;

public class SecurityUtils {

    public static final String[] AUTH_WHITELIST = {
            "/api",
            "/app",
            "/stomp",
            "ws"
    };
}
