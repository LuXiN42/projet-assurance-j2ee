package com.assurance.controller.admin;

import java.io.IOException;
import java.util.List;

import com.assurance.dao.TypeAssuranceDao;
import com.assurance.model.TypeAssurance;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin/types")
public class TypesAssuranceServlet extends HttpServlet {

    private TypeAssuranceDao typeDao = new TypeAssuranceDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<TypeAssurance> types = typeDao.findAllOrderByNom();

        request.setAttribute("types", types);
        request.setAttribute("message", request.getParameter("message"));
        request.setAttribute("erreur", request.getParameter("erreur"));

        request.getRequestDispatcher("/WEB-INF/views/admin/types.jsp").forward(request, response);
    }
}