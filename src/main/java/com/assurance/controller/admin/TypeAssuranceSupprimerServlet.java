package com.assurance.controller.admin;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.assurance.dao.TypeAssuranceDao;
import com.assurance.util.FormUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/types/supprimer")
public class TypeAssuranceSupprimerServlet extends HttpServlet {

    private TypeAssuranceDao typeDao = new TypeAssuranceDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Long id = FormUtil.parseLong(request.getParameter("id"));
        String parametre;

        if (id == null || typeDao.findById(id) == null) {
            parametre = "erreur=" + URLEncoder.encode("Type d'assurance introuvable.", StandardCharsets.UTF_8);
        } else if (typeDao.estUtilise(id)) {
            parametre = "erreur=" + URLEncoder.encode(
                    "Impossible de supprimer ce type : il est utilisé par des contrats.", StandardCharsets.UTF_8);
        } else {
            typeDao.deleteById(id);
            parametre = "message=" + URLEncoder.encode("Type d'assurance supprimé.", StandardCharsets.UTF_8);
        }

        response.sendRedirect(request.getContextPath() + "/admin/types?" + parametre);
    }
}