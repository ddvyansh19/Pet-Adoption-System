package com.petadopt.servlet;

import com.petadopt.exception.AppException;
import com.petadopt.model.User;
import com.petadopt.service.AuthService;
import com.petadopt.util.CsrfUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Login, registration and logout. */
@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/register", "/logout"})
public class AuthServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final transient AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/register":
                view(req, resp, "register.jsp", "Create an account");
                break;
            case "/logout":
                redirect(req, resp, "/login");
                break;
            default:
                User user = currentUser(req);
                if (user != null) {
                    redirect(req, resp, user.getDashboardPath());
                } else {
                    view(req, resp, "login.jsp", "Log in");
                }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/register":
                register(req, resp);
                break;
            case "/logout":
                logout(req, resp);
                break;
            default:
                login(req, resp);
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            User user = authService.login(param(req, "email"), req.getParameter("password"));
            user.setPasswordHash(null); // never keep the hash in the session
            HttpSession session = req.getSession(true);
            req.changeSessionId(); // prevents session fixation
            session.removeAttribute(CsrfUtil.ATTR); // fresh CSRF token for the authenticated session
            session.setAttribute(USER, user);
            session.setMaxInactiveInterval(30 * 60);
            redirect(req, resp, user.getDashboardPath());
        } catch (AppException e) {
            setError(req, e);
            view(req, resp, "login.jsp", "Log in");
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            authService.register(param(req, "name"), param(req, "email"), req.getParameter("password"),
                    req.getParameter("confirm"), param(req, "phone"), param(req, "address"), param(req, "role"));
            flashSuccess(req, "Account created successfully. You can log in now.");
            redirect(req, resp, "/login");
        } catch (AppException e) {
            setError(req, e);
            view(req, resp, "register.jsp", "Create an account");
        }
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        flashSuccess(req, "You have been logged out.");
        redirect(req, resp, "/login");
    }
}
