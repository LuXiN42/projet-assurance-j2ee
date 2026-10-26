package com.assurance.controller.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.assurance.dao.GarantieDao;
import com.assurance.dao.TypeAssuranceDao;
import com.assurance.model.Garantie;
import com.assurance.model.TypeAssurance;
import com.assurance.util.FormUtil;
import com.assurance.util.Validateur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/types/form")
public class TypeAssuranceFormServlet extends HttpServlet {

    private TypeAssuranceDao typeDao = new TypeAssuranceDao();
    private GarantieDao garantieDao = new GarantieDao();

    // Affiche le formulaire : vide pour un ajout, rempli pour une modification
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = FormUtil.parseLong(request.getParameter("id"));
        TypeAssurance type = null;
        Set<Long> idsCoches = new HashSet<>();

        if (id != null) {
            type = typeDao.findByIdWithGaranties(id);
            if (type == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Type introuvable");
                return;
            }
            for (Garantie g : type.getGarantiesDisponibles()) {
                idsCoches.add(g.getId());
            }
        }

        afficherFormulaire(request, response, type, idsCoches, null);
    }

    // Enregistre le type quand on valide le formulaire
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Validateur v = new Validateur();
        Long id             = FormUtil.parseLong(request.getParameter("id"));
        String nom          = v.texte(request.getParameter("nom"), "Le nom", 2, 150, true);
        String description  = v.texte(request.getParameter("description"), "La description", 0, 2000, false);
        BigDecimal prixBase = v.montant(request.getParameter("prixBase"), "Le prix de base", true, false);
        String[] garantiesCochees = request.getParameterValues("garanties");

        if (nom != null) {
            TypeAssurance memeNom = typeDao.findByNom(nom);
            if (memeNom != null && !memeNom.getId().equals(id))
                v.ajouter("Un type d'assurance avec ce nom existe déjà.");
        }

        Set<Garantie> garanties = new HashSet<>();
        Set<Long> idsCoches = new HashSet<>();
        if (garantiesCochees != null) {
            for (String s : garantiesCochees) {
                Long garantieId = FormUtil.parseLong(s);
                Garantie g = (garantieId == null) ? null : garantieDao.findById(garantieId);
                if (g == null) v.ajouter("Une des garanties choisies n'existe pas.");
                else { garanties.add(g); idsCoches.add(garantieId); }
            }
        }

        // S'il y a des erreurs, on réaffiche le formulaire avec ce que l'admin avait tapé
        if (!v.estValide()) {
            TypeAssurance saisie = new TypeAssurance();
            saisie.setId(id);
            saisie.setNom(nom);
            saisie.setDescription(description);
            saisie.setPrixBase(prixBase);
            afficherFormulaire(request, response, saisie, idsCoches, v.getErreurs());
            return;
        }

        // Ajout ou modification
        String message;
        if (id == null) {
            TypeAssurance nouveau = new TypeAssurance();
            nouveau.setNom(nom);
            nouveau.setDescription(description);
            nouveau.setPrixBase(prixBase);
            nouveau.setGarantiesDisponibles(garanties);
            typeDao.save(nouveau);
            message = "Type d'assurance ajouté.";
        } else {
            TypeAssurance existant = typeDao.findByIdWithGaranties(id);
            if (existant == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Type introuvable");
                return;
            }
            existant.setNom(nom);
            existant.setDescription(description);
            existant.setPrixBase(prixBase);
            existant.setGarantiesDisponibles(garanties);
            typeDao.update(existant);
            message = "Type d'assurance modifié.";
        }

        response.sendRedirect(request.getContextPath() + "/admin/types?message="
                + FormUtil.enc(message));
    }

    private void afficherFormulaire(HttpServletRequest request, HttpServletResponse response,
                                    TypeAssurance type, Set<Long> idsCoches, List<String> erreurs)
            throws ServletException, IOException {
        request.setAttribute("type", type);
        request.setAttribute("idsCoches", idsCoches);
        request.setAttribute("erreurs", erreurs);
        request.setAttribute("garanties", garantieDao.findAll());
        request.getRequestDispatcher("/WEB-INF/views/admin/type-form.jsp").forward(request, response);
    }
}