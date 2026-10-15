package com.petadopt.servlet;

import com.petadopt.exception.AppException;
import com.petadopt.model.User;
import com.petadopt.service.AdoptionService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Adopter: submit an application (POST), list own applications and track one by its ID (GET). */
@WebServlet(name = "AdopterApplicationServlet", urlPatterns = "/adopter/applications")
public class AdopterApplicationServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final transient AdoptionService adoptionService = new AdoptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        String trackRaw = param(req, "trackId");
        if (!trackRaw.isEmpty()) {
            try {
                req.setAttribute("tracked", adoptionService.track(me.getId(), idParam(req, "trackId")));
            } catch (AppException e) {
                req.setAttribute("trackError", e.getMessage());
            }
        }
        req.setAttribute("applications", adoptionService.listForAdopter(me.getId()));
        view(req, resp, "adopter/applications.jsp", "My applications");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        long petId = idParam(req, "petId");
        try {
            long id = adoptionService.apply(me.getId(), petId, param(req, "reason"), param(req, "housing"),
                    param(req, "hasOtherPets"), param(req, "experience"));
            flashSuccess(req, "Application submitted successfully! Your application ID is #" + id
                    + ". Use it to track the status.");
            redirect(req, resp, "/adopter/applications?trackId=" + id);
        } catch (AppException e) {
            flashError(req, e);
            redirect(req, resp, "/adopter/pet?id=" + petId);
        }
    }
}
