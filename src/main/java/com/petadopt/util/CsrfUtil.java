package com.petadopt.util;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/** Synchroniser-token CSRF protection: every POST form carries the session's token. */
public final class CsrfUtil {
    public static final String ATTR = "csrfToken";
    public static final String PARAM = "csrf";

    private CsrfUtil() {
    }

    public static String token(HttpSession session) {
        Object existing = session.getAttribute(ATTR);
        if (existing == null) {
            existing = UUID.randomUUID().toString();
            session.setAttribute(ATTR, existing);
        }
        return existing.toString();
    }

    public static boolean isValid(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }
        Object expected = session.getAttribute(ATTR);
        String actual = req.getParameter(PARAM);
        return expected != null && expected.toString().equals(actual);
    }
}
