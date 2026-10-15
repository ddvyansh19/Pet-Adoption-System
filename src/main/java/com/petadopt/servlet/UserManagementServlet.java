package com.petadopt.servlet;

import com.petadopt.exception.AppException;
import com.petadopt.model.Role;
import com.petadopt.model.User;
import com.petadopt.service.UserService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Admin: create, update and delete users and change their roles. */
@WebServlet(name = "UserManagementServlet", urlPatterns = "/admin/users")
public class UserManagementServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final transient UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long editId = idParam(req, "edit");
        if (editId > 0) {
            try {
                req.setAttribute("formUser", userService.get(editId));
            } catch (AppException e) {
                setError(req, e);
            }
        }
        render(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        String action = param(req, "action");
        try {
            switch (action) {
                case "create":
                    userService.create(param(req, "name"), param(req, "email"), req.getParameter("password"),
                            param(req, "role"), param(req, "phone"), param(req, "address"));
                    flashSuccess(req, "User created successfully.");
                    break;
                case "update":
                    userService.update(idParam(req, "id"), param(req, "name"), param(req, "email"),
                            param(req, "role"), param(req, "phone"), param(req, "address"),
                            req.getParameter("password"), me.getId());
                    flashSuccess(req, "User updated successfully.");
                    break;
                case "delete":
                    userService.delete(idParam(req, "id"), me.getId());
                    flashSuccess(req, "User deleted successfully.");
                    break;
                default:
                    flashError(req, "Unknown action.");
            }
            redirect(req, resp, "/admin/users");
        } catch (AppException e) {
            if ("delete".equals(action)) {
                flashError(req, e);
                redirect(req, resp, "/admin/users");
                return;
            }
            // keep what the admin typed and show the form again with the problems
            setError(req, e);
            req.setAttribute("formUser", formFromParams(req));
            render(req, resp);
        }
    }

    private void render(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String roleFilter = param(req, "role_filter");
        try {
            req.setAttribute("users", userService.list(roleFilter));
        } catch (AppException e) {
            roleFilter = "";
            req.setAttribute("users", userService.list(null));
        }
        req.setAttribute("roleFilter", roleFilter);
        req.setAttribute("roles", Role.values());
        view(req, resp, "admin/users.jsp", "User management");
    }

    private User formFromParams(HttpServletRequest req) {
        Role role;
        try {
            role = Role.valueOf(param(req, "role"));
        } catch (IllegalArgumentException e) {
            role = Role.ADOPTER;
        }
        User form = User.create(role);
        form.setId(idParam(req, "id"));
        form.setName(param(req, "name"));
        form.setEmail(param(req, "email"));
        form.setPhone(param(req, "phone"));
        form.setAddress(param(req, "address"));
        return form;
    }
}
