package com.petadopt.servlet;

import com.petadopt.exception.AppException;
import com.petadopt.exception.DataAccessException;
import com.petadopt.exception.ValidationException;
import com.petadopt.model.Role;
import com.petadopt.model.User;
import com.petadopt.service.MessageService;
import com.petadopt.service.SettingService;
import com.petadopt.util.CsrfUtil;
import com.petadopt.util.Validator;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/** Common helpers for every servlet: session user, flash messages, CSRF check, view forwarding. */
public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected static final String VIEWS = "/WEB-INF/views/";
    public static final String USER = "user";
    private static final Logger LOG = Logger.getLogger(BaseServlet.class.getName());

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if ("POST".equals(req.getMethod()) && !CsrfUtil.isValid(req)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Your session security token is missing or expired. Please go back, reload the page and try again.");
            return;
        }
        super.service(req, resp);
    }

    // ------------------------------------------------------------ session helpers
    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(USER);
    }

    protected void flashSuccess(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashSuccess", message);
    }

    protected void flashError(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flashError", message);
    }

    protected void flashError(HttpServletRequest req, AppException e) {
        flashError(req, messageOf(e));
    }

    // ------------------------------------------------------------ errors
    protected String messageOf(AppException e) {
        if (e instanceof DataAccessException) {
            LOG.log(Level.SEVERE, "Database error", e.getCause());
        }
        return e.getMessage();
    }

    /** Puts the error on the request so the next forwarded view can show it. */
    protected void setError(HttpServletRequest req, AppException e) {
        if (e instanceof ValidationException) {
            req.setAttribute("errors", ((ValidationException) e).getErrors());
        } else {
            req.setAttribute("error", messageOf(e));
        }
    }

    // ------------------------------------------------------------ navigation
    protected void view(HttpServletRequest req, HttpServletResponse resp, String page, String title)
            throws ServletException, IOException {
        req.setAttribute("pageTitle", title);
        req.setAttribute("ctx", req.getContextPath());
        HttpSession session = req.getSession();
        CsrfUtil.token(session); // makes ${sessionScope.csrfToken} available to every form
        try {
            req.setAttribute("siteName", SettingService.getInstance().get(SettingService.SITE_NAME));
            req.setAttribute("contactEmail", SettingService.getInstance().get(SettingService.CONTACT_EMAIL));
            User user = currentUser(req);
            if (user != null && user.getRole() != Role.ADMIN) {
                req.setAttribute("unreadCount", new MessageService().unreadCount(user.getId()));
            }
        } catch (AppException e) {
            req.setAttribute("siteName", "Paws & Homes"); // the page must still render if the DB hiccups
        }
        req.getRequestDispatcher(VIEWS + page).forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    // ------------------------------------------------------------ parameters
    protected String param(HttpServletRequest req, String name) {
        return Validator.clean(req.getParameter(name));
    }

    /** @return the numeric parameter or -1 when missing/invalid (real IDs start at 1). */
    protected long idParam(HttpServletRequest req, String name) {
        try {
            return Long.parseLong(param(req, name));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
