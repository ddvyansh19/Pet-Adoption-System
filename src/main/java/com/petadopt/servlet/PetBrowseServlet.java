package com.petadopt.servlet;

import com.petadopt.service.PetService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Adopter: search the approved, available pets. */
@WebServlet(name = "PetBrowseServlet", urlPatterns = "/adopter/browse")
public class PetBrowseServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final transient PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = param(req, "type");
        String breed = param(req, "breed");
        String location = param(req, "location");
        req.setAttribute("pets", petService.search(type, breed, location));
        req.setAttribute("petTypes", PetService.TYPES);
        req.setAttribute("searched", !(type.isEmpty() && breed.isEmpty() && location.isEmpty()));
        view(req, resp, "adopter/browse.jsp", "Browse pets");
    }
}
