<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mes sinistres</title>
</head>
<body>

<p><a href="${ctx}/client/accueil">← Accueil</a></p>

<h1>Mes sinistres</h1>

<p><a href="${ctx}/client/sinistres/declarer">+ Déclarer un sinistre</a></p>

<c:choose>
    <c:when test="${empty sinistres}">
        <p>Vous n'avez déclaré aucun sinistre.</p>
    </c:when>
    <c:otherwise>
        <table border="1" cellpadding="6">
            <tr>
                <th>Numéro</th>
                <th>Date</th>
                <th>Contrat</th>
                <th>Type</th>
                <th>Statut</th>
                <th></th>
            </tr>
            <c:forEach var="s" items="${sinistres}">
                <tr>
                    <td><c:out value="${s.numeroSinistre}"/></td>
                    <td><c:out value="${s.date}"/></td>
                    <td><c:out value="${s.contrat.numeroContrat}"/></td>
                    <td><c:out value="${s.typeSinistre}"/></td>
                    <td><c:out value="${s.statut}"/></td>
                    <td><a href="${ctx}/client/sinistres/detail?id=${s.id}">Voir</a></td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

</body>
</html>