package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * cookie工具类
 */
public class CookieUtil {

    /**
     * 从 request 请求中取出 cookie
     * @param request
     * @param key
     * @return
     */
    public static String getValue(HttpServletRequest request, String key){
        if (request == null || key == null) {
            throw new IllegalArgumentException("参数为空!");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
