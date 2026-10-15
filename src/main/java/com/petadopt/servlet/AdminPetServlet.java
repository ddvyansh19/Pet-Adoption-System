package com.petadopt.servlet;

import com.petadopt.exception.AppException;
import com.petadopt.model.Pet;
import com.petadopt.service.PetService;
import java.io.IOException;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Admin: approve or reject the pet listings submitted by shelters. */
@WebServlet(name = "AdminPetServlet", urlPatterns = "/admin/pets")
public class AdminPetServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static final Set<String> FILTERS = Set.of("PENDING", "APPROVED", "REJECTED");
    private final transient PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String status = param(req, "status");
        if (!FILTERS.contains(status)) {
            status = "";
        }
        req.setAttribute("status", status);
        req.setAttribute("pets", petService.listForAdmin(status));
        view(req, resp, "admin/pets.jsp", "Pet listings");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean approve = "approve".equals(param(req, "action"));
        try {
            Pet pet = petService.decide(idParam(req, "id"), approve);
            flashSuccess(req, "Listing for " + pet.getName() + (approve ? " was approved." : " was rejected."));
        } catch (AppException e) {
            flashError(req, e);
        }
        String status = param(req, "returnStatus");
        redirect(req, resp, "/admin/pets" + (FILTERS.contains(status) ? "?status=" + status : ""));
    }
}
