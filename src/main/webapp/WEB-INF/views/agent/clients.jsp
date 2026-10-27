<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Clients · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">
    <h1>Clients</h1>

    <form method="get" action="${ctx}/agent/clients">
        <input type="text" name="q" value="<c:out value='${q}'/>"
               placeholder="Nom, prénom ou email">
        <button type="submit">Rechercher</button>
        <c:if test="${not empty q}">
            <a href="${ctx}/agent/clients">Réinitialiser</a>
        </c:if>
    </form>

    <p>${clients.size()} client(s) trouvé(s)</p>

    <c:choose>
        <c:when test="${empty clients}">
            <p>Aucun client ne correspond à votre recherche.</p>
        </c:when>
        <c:otherwise>
            <table border="1" cellpadding="6">
                <thead>
                    <tr>
                        <th>Nom</th>
                        <th>Prénom</th>
                        <th>Email</th>
                        <th>Inscrit le</th>
                        <th>Contrats</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="cl" items="${clients}">
                        <tr>
                            <td><c:out value="${cl.nom}"/></td>
                            <td><c:out value="${cl.prenom}"/></td>
                            <td><c:out value="${cl.email}"/></td>
                            <td>${cl.dateInscription}</td>
                            <td><a href="${ctx}/agent/contrats?clientId=${cl.id}">Voir</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>