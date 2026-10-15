package com.petadopt.servlet;

import com.petadopt.exception.AppException;
import com.petadopt.exception.ValidationException;
import com.petadopt.model.AdoptionStatus;
import com.petadopt.model.Pet;
import com.petadopt.model.User;
import com.petadopt.service.PetService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/** Shelter: list, edit and remove pets (with photo upload). */
@WebServlet(name = "ShelterPetServlet", urlPatterns = "/shelter/pets")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5L * 1024 * 1024, maxRequestSize = 6L * 1024 * 1024)
public class ShelterPetServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final transient PetService petService = new PetService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        String action = param(req, "action");
        try {
            if ("new".equals(action)) {
                showForm(req, resp, new Pet(), false);
                return;
            }
            if ("edit".equals(action)) {
                showForm(req, resp, petService.ownedPet(me.getId(), idParam(req, "id")), true);
                return;
            }
        } catch (AppException e) {
            flashError(req, e);
            redirect(req, resp, "/shelter/pets");
            return;
        }
        req.setAttribute("pets", petService.listForShelter(me.getId()));
        view(req, resp, "shelter/pets.jsp", "My pets");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        String action = param(req, "action");
        boolean editing = "update".equals(action);
        try {
            switch (action) {
                case "create":
                    petService.create(me.getId(), param(req, "name"), param(req, "type"), param(req, "breed"),
                            param(req, "age"), param(req, "location"), param(req, "description"), photo(req));
                    flashSuccess(req, "Pet listed successfully. It will be visible to adopters once an admin approves it.");
                    break;
                case "update":
                    petService.update(me.getId(), idParam(req, "id"), param(req, "name"), param(req, "type"),
                            param(req, "breed"), param(req, "age"), param(req, "location"),
                            param(req, "description"), param(req, "adoptionStatus"), photo(req));
                    flashSuccess(req, "Pet details updated successfully.");
                    break;
                case "delete":
                    petService.delete(me.getId(), idParam(req, "id"));
                    flashSuccess(req, "Pet listing removed.");
                    break;
                default:
                    flashError(req, "Unknown action.");
            }
            redirect(req, resp, "/shelter/pets");
        } catch (AppException e) {
            if ("delete".equals(action) || (!"create".equals(action) && !editing)) {
                flashError(req, e);
                redirect(req, resp, "/shelter/pets");
                return;
            }
            setError(req, e);
            showForm(req, resp, formFromParams(req), editing);
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Pet pet, boolean editing)
            throws ServletException, IOException {
        req.setAttribute("formPet", pet);
        req.setAttribute("editing", editing);
        req.setAttribute("petTypes", PetService.TYPES);
        view(req, resp, "shelter/pet-form.jsp", editing ? "Edit pet" : "List a new pet");
    }

    private Part photo(HttpServletRequest req) throws IOException, ServletException {
        String contentType = req.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("multipart/")) {
            return null; // not a file upload request: nothing to store
        }
        try {
            return req.getPart("photo");
        } catch (IllegalStateException e) {
            throw new ValidationException("The photo is too large. The maximum size is 5 MB.");
        }
    }

    private Pet formFromParams(HttpServletRequest req) {
        Pet pet = new Pet();
        pet.setId(idParam(req, "id"));
        pet.setName(param(req, "name"));
        pet.setType(param(req, "type"));
        pet.setBreed(param(req, "breed"));
        try {
            pet.setAgeYears(Double.parseDouble(param(req, "age")));
        } catch (NumberFormatException e) {
            pet.setAgeYears(0);
        }
        pet.setLocation(param(req, "location"));
        pet.setDescription(param(req, "description"));
        try {
            pet.setAdoptionStatus(AdoptionStatus.valueOf(param(req, "adoptionStatus")));
        } catch (IllegalArgumentException e) {
            pet.setAdoptionStatus(AdoptionStatus.AVAILABLE);
        }
        return pet;
    }
}
