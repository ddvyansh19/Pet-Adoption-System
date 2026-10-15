package com.petadopt.servlet;

import com.petadopt.exception.AppException;
import com.petadopt.model.Pet;
import com.petadopt.model.User;
import com.petadopt.service.AdoptionService;
import com.petadopt.service.PetService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Adopter: one pet's page, including the adoption application form. */
@WebServlet(name = "PetDetailServlet", urlPatterns = "/adopter/pet")
public class PetDetailServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final transient PetService petService = new PetService();
    private final transient AdoptionService adoptionService = new AdoptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        try {
            Pet pet = petService.getPublic(idParam(req, "id"));
            req.setAttribute("pet", pet);
            req.setAttribute("alreadyApplied", adoptionService.hasApplied(me.getId(), pet.getId()));
            req.setAttribute("housingTypes", AdoptionService.HOUSING_TYPES);
            view(req, resp, "adopter/pet-detail.jsp", pet.getName());
        } catch (AppException e) {
            flashError(req, e);
            redirect(req, resp, "/adopter/browse");
        }
    }
}
