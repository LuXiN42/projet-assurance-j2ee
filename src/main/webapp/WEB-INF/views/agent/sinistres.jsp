<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Sinistres</title>
</head>
<body>

<p><a href="${ctx}/agent/accueil">← Accueil agent</a></p>

<h1>Sinistres</h1>

<form method="get" action="${ctx}/agent/sinistres">
    <label>Statut :
        <select name="statut">
            <option value="">Tous</option>
            <c:forEach var="st" items="${statuts}">
                <option value="${st}" <c:if test="${st == statutChoisi}">selected</c:if>>${st}</option>
            </c:forEach>
        </select>
    </label>
    <button type="submit">Filtrer</button>
</form>

<c:choose>
    <c:when test="${empty sinistres}">
        <p>Aucun sinistre.</p>
    </c:when>
    <c:otherwise>
        <table border="1" cellpadding="6">
            <tr>
                <th>Numéro</th>
                <th>Date</th>
                <th>Client</th>
                <th>Contrat</th>
                <th>Type</th>
                <th>Montant estimé</th>
                <th>Statut</th>
                <th></th>
            </tr>
            <c:forEach var="s" items="${sinistres}">
                <tr>
                    <td><c:out value="${s.numeroSinistre}"/></td>
                    <td><c:out value="${s.date}"/></td>
                    <td><c:out value="${s.contrat.client.nom}"/> <c:out value="${s.contrat.client.prenom}"/></td>
                    <td><c:out value="${s.contrat.numeroContrat}"/></td>
                    <td><c:out value="${s.typeSinistre}"/></td>
                    <td><c:out value="${s.montantEstime}"/> €</td>
                    <td><c:out value="${s.statut}"/></td>
                    <td><a href="${ctx}/agent/sinistre?id=${s.id}">Traiter</a></td>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

</body>
</html>