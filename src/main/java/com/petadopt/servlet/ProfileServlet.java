package com.petadopt.servlet;

import com.petadopt.exception.AppException;
import com.petadopt.model.User;
import com.petadopt.service.UserService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Adopter: update contact details and change the password. */
@WebServlet(name = "ProfileServlet", urlPatterns = "/adopter/profile")
public class ProfileServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final transient UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        view(req, resp, "adopter/profile.jsp", "My profile");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        try {
            if ("password".equals(param(req, "action"))) {
                userService.changePassword(me.getId(), req.getParameter("current"), req.getParameter("next"),
                        req.getParameter("confirm"));
                flashSuccess(req, "Password changed successfully.");
            } else {
                User updated = userService.updateProfile(me.getId(), param(req, "name"), param(req, "phone"),
                        param(req, "address"));
                updated.setPasswordHash(null);
                req.getSession().setAttribute(USER, updated); // header shows the new name straight away
                flashSuccess(req, "Profile updated successfully.");
            }
            redirect(req, resp, "/adopter/profile");
        } catch (AppException e) {
            setError(req, e);
            view(req, resp, "adopter/profile.jsp", "My profile");
        }
    }
}
