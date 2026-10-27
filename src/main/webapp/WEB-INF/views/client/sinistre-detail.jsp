<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Sinistre ${sinistre.numeroSinistre} · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">

<p><a href="${ctx}/client/sinistres">← Mes sinistres</a></p>

<h1>Sinistre <c:out value="${sinistre.numeroSinistre}"/></h1>

<table border="1" cellpadding="6">
    <tr><th>Statut</th>         <td><strong><c:out value="${sinistre.statut}"/></strong></td></tr>
    <tr><th>Date</th>           <td><c:out value="${sinistre.date}"/></td></tr>
    <tr><th>Contrat</th>        <td><c:out value="${sinistre.contrat.numeroContrat}"/>
                                    (<c:out value="${sinistre.contrat.typeAssurance.nom}"/>)</td></tr>
    <tr><th>Type</th>           <td><c:out value="${sinistre.typeSinistre}"/></td></tr>
    <tr><th>Description</th>    <td><c:out value="${sinistre.description}"/></td></tr>
    <tr><th>Montant estimé</th> <td><c:out value="${sinistre.montantEstime}"/> €</td></tr>
    <c:if test="${not empty sinistre.montantIndemnisation}">
        <tr><th>Montant indemnisé</th> <td><c:out value="${sinistre.montantIndemnisation}"/> €</td></tr>
    </c:if>
</table>

<h2>Où en est mon dossier ?</h2>
<c:choose>
    <c:when test="${sinistre.statut == 'DECLARE'}">
        <p>Votre déclaration a bien été reçue. Elle sera bientôt prise en charge par un agent.</p>
    </c:when>
    <c:when test="${sinistre.statut == 'EN_ANALYSE'}">
        <p>Un agent est en train d'étudier votre dossier.</p>
    </c:when>
    <c:when test="${sinistre.statut == 'ACCEPTE'}">
        <p>Votre sinistre a été accepté. L'indemnisation est en cours.</p>
    </c:when>
    <c:when test="${sinistre.statut == 'REFUSE'}">
        <p>Votre sinistre a été refusé. Contactez votre agent pour plus d'informations.</p>
    </c:when>
    <c:when test="${sinistre.statut == 'INDEMNISE'}">
        <p>Votre sinistre a été indemnisé. Le dossier est clos.</p>
    </c:when>
</c:choose>

<h2>Pièces justificatives</h2>
<c:choose>
    <c:when test="${empty sinistre.pieces}">
        <p>Aucune pièce jointe.</p>
    </c:when>
    <c:otherwise>
        <ul>
            <c:forEach var="p" items="${sinistre.pieces}">
                <li><a href="${ctx}/piece?id=${p.id}" target="_blank"><c:out value="${p.nomOriginal}"/></a></li>
            </c:forEach>
        </ul>
    </c:otherwise>
</c:choose>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>