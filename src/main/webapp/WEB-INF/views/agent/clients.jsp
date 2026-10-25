<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Clients</title>
</head>
<body>
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

    <p><a href="${ctx}/agent/accueil">Retour à l'accueil</a></p>
</body>
</html>