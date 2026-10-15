package com.petadopt.filter;

import com.petadopt.model.Role;
import com.petadopt.model.User;
import com.petadopt.servlet.BaseServlet;
import java.io.IOException;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Step 2 of access control: /admin/* is for admins, /shelter/* for shelters, /adopter/* for adopters. */
public class RoleFilter implements Filter {
    private static final Map<String, Role> PROTECTED_AREAS = Map.of(
            "/admin", Role.ADMIN,
            "/shelter", Role.SHELTER,
            "/adopter", Role.ADOPTER);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();
        Role required = PROTECTED_AREAS.entrySet().stream()
                .filter(e -> path.equals(e.getKey()) || path.startsWith(e.getKey() + "/"))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);

        User user = (User) req.getSession().getAttribute(BaseServlet.USER); // AuthFilter guarantees a session
        if (required != null && (user == null || user.getRole() != required)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(request, response);
    }
}
