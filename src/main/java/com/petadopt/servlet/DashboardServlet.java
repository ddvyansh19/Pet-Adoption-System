package com.petadopt.servlet;

import com.petadopt.model.Application;
import com.petadopt.model.User;
import com.petadopt.service.AdoptionService;
import com.petadopt.service.AnalyticsService;
import com.petadopt.service.PetService;
import com.petadopt.service.UserService;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** One servlet, three dashboards: the URL decides which role's dashboard is built. */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/admin/dashboard", "/shelter/dashboard", "/adopter/dashboard"})
public class DashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private final transient AnalyticsService analytics = new AnalyticsService();
    private final transient PetService petService = new PetService();
    private final transient UserService userService = new UserService();
    private final transient AdoptionService adoptionService = new AdoptionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User me = currentUser(req);
        Map<String, Object> model;
        String page;
        String title;
        switch (req.getServletPath()) {
            case "/admin/dashboard":
                model = analytics.adminDashboard();
                req.setAttribute("pendingPets",
                        petService.listForAdmin("PENDING").stream().limit(5).collect(Collectors.toList()));
                req.setAttribute("recentUsers",
                        userService.list(null).stream().limit(5).collect(Collectors.toList()));
                page = "admin/dashboard.jsp";
                title = "Admin dashboard";
                break;
            case "/shelter/dashboard":
                model = analytics.shelterDashboard(me.getId());
                req.setAttribute("pendingApplications", adoptionService.listForShelter(me.getId(), "PENDING")
                        .stream().limit(5).collect(Collectors.toList()));
                req.setAttribute("recentPets",
                        petService.listForShelter(me.getId()).stream().limit(4).collect(Collectors.toList()));
                page = "shelter/dashboard.jsp";
                title = "Shelter dashboard";
                break;
            default:
                model = analytics.adopterDashboard(me.getId());
                req.setAttribute("recentApplications", adoptionService.listForAdopter(me.getId()).stream()
                        .limit(5).collect(Collectors.<Application>toList()));
                page = "adopter/dashboard.jsp";
                title = "My dashboard";
        }
        model.forEach(req::setAttribute);
        view(req, resp, page, title);
    }
}
