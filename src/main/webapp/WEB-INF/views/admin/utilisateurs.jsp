<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Utilisateurs · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">

<h1>Utilisateurs</h1>

<c:if test="${not empty message}">
    <p class="alerte ok"><c:out value="${message}"/></p>
</c:if>
<c:if test="${not empty erreur}">
    <p class="alerte ko"><c:out value="${erreur}"/></p>
</c:if>

<p><a href="${ctx}/admin/utilisateurs/form">+ Créer un utilisateur</a></p>

<form method="get" action="${ctx}/admin/utilisateurs">
    <label>Rôle :
        <select name="role">
            <option value="">Tous</option>
            <c:forEach var="r" items="${roles}">
                <option value="${r}" <c:if test="${r == roleChoisi}">selected</c:if>>${r}</option>
            </c:forEach>
        </select>
    </label>
    <button type="submit">Filtrer</button>
</form>

<c:choose>
    <c:when test="${empty utilisateurs}">
        <p>Aucun utilisateur.</p>
    </c:when>
    <c:otherwise>
        <table border="1" cellpadding="6">
            <tr>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Email</th>
                <th>Rôle</th>
                <th>Inscrit le</th>
                <th>Actions</th>
            </tr>
            <c:forEach var="u" items="${utilisateurs}">
                <tr>
                    <td><c:out value="${u.nom}"/></td>
                    <td><c:out value="${u.prenom}"/></td>
                    <td><c:out value="${u.email}"/></td>
                    <td><c:out value="${u.role}"/></td>
                    <td><c:out value="${u.dateInscription}"/></td>
                    <td>
                        <a href="${ctx}/admin/utilisateurs/form?id=${u.id}">Modifier</a>

                        <c:if test="${u.id != sessionScope.utilisateur.id}">
                            <form method="post" action="${ctx}/admin/utilisateurs/supprimer" style="display:inline"
                                  onsubmit="return confirm('Supprimer cet utilisateur ?');">
                                <input type="hidden" name="id" value="${u.id}">
                                <button type="submit">Supprimer</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>