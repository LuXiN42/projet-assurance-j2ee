package com.assurance.controller;

import com.assurance.dao.PieceJustificativeDao;
import com.assurance.model.*;
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
        Utilisateur u = session == null ? null : (Utilisateur) session.getAttribute("utilisateur");
        if (u == null) { resp.sendRedirect(req.getContextPath() + "/login"); return; }

        PieceJustificative p;
        try {
            p = dao.findByIdAvecProprietaire(Long.parseLong(req.getParameter("id")));
        } catch (NumberFormatException e) { resp.sendError(400); return; }
        if (p == null) { resp.sendError(404); return; }

        boolean proprietaire = p.getSinistre().getContrat().getClient().getId().equals(u.getId());
        if (u.getRole() == Role.CLIENT && !proprietaire) { resp.sendError(403); return; }

        Path fichier = UploadConfig.DOSSIER.resolve(p.getNomStocke());
        if (!Files.exists(fichier)) { resp.sendError(404); return; }

        resp.setContentType(p.getTypeMime());
        resp.setContentLengthLong(Files.size(fichier));
        resp.setHeader("Content-Disposition", "inline; filename=\"" + p.getNomOriginal() + "\"");
        Files.copy(fichier, resp.getOutputStream());
    }
}