package com.petadopt.servlet;

import com.petadopt.exception.AppException;
import com.petadopt.service.SettingService;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Admin: system-wide settings. */
@WebServlet(name = "SettingsServlet", urlPatterns = "/admin/settings")
public class SettingsServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final transient SettingService settings = SettingService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("formSettings", settings.all());
        view(req, resp, "admin/settings.jsp", "System settings");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            settings.update(param(req, "site_name"), param(req, "contact_email"), param(req, "allow_registration"),
                    param(req, "max_active_applications"));
            flashSuccess(req, "Settings updated successfully.");
            redirect(req, resp, "/admin/settings");
        } catch (AppException e) {
            setError(req, e);
            Map<String, String> typed = new LinkedHashMap<>();
            typed.put(SettingService.SITE_NAME, param(req, "site_name"));
            typed.put(SettingService.CONTACT_EMAIL, param(req, "contact_email"));
            typed.put(SettingService.ALLOW_REGISTRATION, param(req, "allow_registration"));
            typed.put(SettingService.MAX_ACTIVE_APPLICATIONS, param(req, "max_active_applications"));
            req.setAttribute("formSettings", typed);
            view(req, resp, "admin/settings.jsp", "System settings");
        }
    }
}
