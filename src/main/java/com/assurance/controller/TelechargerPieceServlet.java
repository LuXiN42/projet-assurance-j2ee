package com.assurance.controller;

import com.assurance.dao.PieceJustificativeDao;
import com.assurance.model.*;
import com.assurance.util.FormUtil;
import com.assurance.util.SessionKeys;
import com.assurance.util.UploadConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.file.*;

@WebServlet("/piece")
public class TelechargerPieceServlet extends HttpServlet {
    private final PieceJustificativeDao dao = new PieceJustificativeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        Utilisateur u = session == null ? null : (Utilisateur) session.getAttribute(SessionKeys.UTILISATEUR);
        if (u == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        Long id = FormUtil.parseLong(req.getParameter("id"));
        if (id == null) { resp.sendError(HttpServletResponse.SC_BAD_REQUEST); return; }

        PieceJustificative p = dao.findByIdAvecProprietaire(id);
        if (p == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

        // Un client ne peut télécharger que les pièces de ses propres sinistres
        boolean proprietaire = p.getSinistre().getContrat().getClient().getId().equals(u.getId());
        if (u.getRole() == Role.CLIENT && !proprietaire) { resp.sendError(HttpServletResponse.SC_FORBIDDEN); return; }

        Path fichier = UploadConfig.DOSSIER.resolve(p.getNomStocke());
        if (!Files.exists(fichier)) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }

        resp.setContentType(p.getTypeMime());
        resp.setContentLengthLong(Files.size(fichier));
        resp.setHeader("Content-Disposition", "inline; filename=\"" + p.getNomOriginal() + "\"");
        Files.copy(fichier, resp.getOutputStream());
    }
}