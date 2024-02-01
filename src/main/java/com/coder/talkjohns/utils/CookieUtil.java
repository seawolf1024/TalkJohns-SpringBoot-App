package com.coder.talkjohns.utils;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name){
        if(request == null || name == null){
            throw new IllegalArgumentException("Empty parameters!");
        }

        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie: cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;

    }
}
