package com.tekin.app.web.util;

import jakarta.servlet.http.HttpServletRequest;

public class RequestContext {
    public static String role(HttpServletRequest req) {
        String r = req.getHeader("X-Demo-Role");
        return r == null ? "COWORKER" : r;
        }
    public static Long userId(HttpServletRequest req) {
        try {
            String v = req.getHeader("X-Demo-UserId");
            return v == null ? 0L : Long.parseLong(v);
        } catch (Exception e) {
            return 0L;
        }
    }
}
