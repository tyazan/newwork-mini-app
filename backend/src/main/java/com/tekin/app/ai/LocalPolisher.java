package com.tekin.app.ai;

import org.springframework.stereotype.Component;

@Component
public class LocalPolisher implements TextPolisher {
    @Override public String polish(String s) {
        if (s == null) return "";
        String t = s.replaceAll("\\s+", " ").trim();
        t = t.replaceAll("([A-Za-z])\\1{2,}", "$1");
        t = t.replaceAll("!{2,}", "!").replaceAll("\\?{2,}", "?");
        t = t.replaceAll("([!?])\\.", "$1");
        if (!t.isEmpty() && Character.isLetter(t.charAt(0)) && Character.isLowerCase(t.charAt(0))) {
            t = Character.toUpperCase(t.charAt(0)) + t.substring(1);
        }
        if (!t.isEmpty() && !t.matches(".*[.!?]$")) t = t + ".";
        return t;
    }
}
