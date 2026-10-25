<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Types d'assurance</title>
</head>
<body>

<p><a href="${ctx}/admin/accueil">← Accueil admin</a></p>

<h1>Types d'assurance</h1>

<c:if test="${not empty message}">
    <p style="color:green"><c:out value="${message}"/></p>
</c:if>
<c:if test="${not empty erreur}">
    <p style="color:red"><c:out value="${erreur}"/></p>
</c:if>

<p><a href="${ctx}/admin/types/form">+ Ajouter un type d'assurance</a></p>

<c:choose>
    <c:when test="${empty types}">
        <p>Aucun type d'assurance pour le moment.</p>
    </c:when>
    <c:otherwise>
        <table border="1" cellpadding="6">
            <tr>
                <th>Nom</th>
                <th>Description</th>
                <th>Prix de base</th>
                <th>Actions</th>
            </tr>
            <c:forEach var="t" items="${types}">
                <tr>
                    <td><c:out value="${t.nom}"/></td>
                    <td><c:out value="${t.description}"/></td>
                    <td><c:out value="${t.prixBase}"/> €</td>
                    <td>
                        <a href="${ctx}/admin/types/form?id=${t.id}">Modifier</a>

                        <form method="post" action="${ctx}/admin/types/supprimer" style="display:inline"
                              onsubmit="return confirm('Supprimer ce type d\'assurance ?');">
                            <input type="hidden" name="id" value="${t.id}">
                            <button type="submit">Supprimer</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

</body>
</html>