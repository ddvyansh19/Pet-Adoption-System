package com.petadopt.servlet;

import com.petadopt.model.Application;
import com.petadopt.model.User;
import com.petadopt.service.AdoptionService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Adopter: adopted pets plus every past application. */
@WebServlet(name = "HistoryServlet", urlPatterns = "/adopter/history")
public class HistoryServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final transient AdoptionService adoptionService = new AdoptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        List<Application> all = adoptionService.listForAdopter(me.getId());
        Map<Boolean, List<Application>> parts = adoptionService.splitAdoptedAndOther(all);
        req.setAttribute("adopted", parts.get(true));
        req.setAttribute("otherApplications", parts.get(false));
        view(req, resp, "adopter/history.jsp", "Adoption history");
    }
}
