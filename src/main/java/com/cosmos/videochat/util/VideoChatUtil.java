package com.cosmos.videochat.util;

public interface VideoChatUtil {

    // user source
    public static String MOBILE = "mobile";

    public static String WEB = "web";

    static final String[] ALLOWED_ORIGINS = {
            "http://3.1.43.143:3000", // aws
            "http://localhost:3000",
            "http://localhost:5000",
            "http://localhost:3001",
            "http://localhost:8081",
            "http://localhost:4200",
            "amtrixtech.com.np",
            "http://192.168.1.21:3001",
            "http://192.168.1.21:3000",
            "https://amtrixtech.com.np",
            "https://backend.amtrixtech.com.np",
            "https://pharmedica.amtrixtech.com.np",
            "https://v2.chiragthapa.com.np",
            "https://admin.amtrixtech.com.np",
            "https://admin.chiragthapa.com.np",
            "https://pharmedica.chiragthapa.com.np",
            "https://system.cosmosastrology.com"
    };

}
