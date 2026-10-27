package com.assurance.filter;

import com.assurance.model.Role;
import com.assurance.model.Utilisateur;
import com.assurance.util.SessionKeys;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/client/*", "/agent/*", "/admin/*", "/profil"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        Utilisateur u = session == null ? null : (Utilisateur) session.getAttribute(SessionKeys.UTILISATEUR);

        // pas connecté : retour au login
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // connecté mais pas le bon rôle : 403
        String chemin = req.getRequestURI().substring(req.getContextPath().length());
        if (!autorise(chemin, u.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Accès refusé");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean autorise(String chemin, Role role) {
        if (chemin.startsWith("/admin/"))  return role == Role.ADMIN;
        if (chemin.startsWith("/agent/"))  return role == Role.AGENT || role == Role.ADMIN;
        if (chemin.startsWith("/client/")) return role == Role.CLIENT;
        return true;
    }
}