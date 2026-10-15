package com.petadopt.filter;

import com.petadopt.servlet.BaseServlet;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Step 1 of access control: the visitor must be logged in. */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        boolean loggedIn = session != null && session.getAttribute(BaseServlet.USER) != null;
        if (!loggedIn) {
            req.getSession(true).setAttribute("flashError", "Please log in to continue.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        // keep private pages out of the browser cache so "Back" after logout shows nothing
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        chain.doFilter(request, response);
    }
}
