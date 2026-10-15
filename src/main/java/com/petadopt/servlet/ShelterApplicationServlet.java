package com.petadopt.servlet;

import com.petadopt.exception.AppException;
import com.petadopt.model.Application;
import com.petadopt.model.User;
import com.petadopt.service.AdoptionService;
import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Shelter: review adoption applications and approve or reject them. */
@WebServlet(name = "ShelterApplicationServlet", urlPatterns = "/shelter/applications")
public class ShelterApplicationServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static final Set<String> FILTERS = Set.of("PENDING", "APPROVED", "REJECTED");
    private final transient AdoptionService adoptionService = new AdoptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        String status = param(req, "status");
        if (!FILTERS.contains(status)) {
            status = "";
        }
        req.setAttribute("status", status);
        req.setAttribute("applications", adoptionService.listForShelter(me.getId(), status));
        view(req, resp, "shelter/applications.jsp", "Adoption applications");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        boolean approve = "approve".equals(param(req, "action"));
        try {
            Application app = adoptionService.decide(me.getId(), idParam(req, "id"), approve, param(req, "note"));
            flashSuccess(req, approve
                    ? "Application #" + app.getId() + " approved. " + app.getPetName()
                            + " is now marked as adopted and competing applications were closed."
                    : "Application #" + app.getId() + " was rejected.");
        } catch (AppException e) {
            flashError(req, e);
        }
        String status = param(req, "returnStatus");
        redirect(req, resp, "/shelter/applications" + (FILTERS.contains(status) ? "?status=" + status : ""));
    }
}
