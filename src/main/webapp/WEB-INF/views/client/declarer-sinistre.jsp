<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Déclarer un sinistre · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">
<h2>Déclarer un sinistre</h2>

<%-- message d'erreur de la servlet, affiché avec c:out pour éviter le XSS --%>
<c:if test="${not empty erreur}">
    <p class="alerte ko"><c:out value="${erreur}"/></p>
</c:if>

<c:choose>
<c:when test="${empty contrats}">
    <p>Vous n'avez aucun contrat actif : impossible de déclarer un sinistre.</p>
</c:when>
<c:otherwise>
<form method="post" enctype="multipart/form-data"
      action="${pageContext.request.contextPath}/client/sinistres/declarer">

    <label>Contrat</label><br>
    <select name="contratId" required>
        <c:forEach var="ct" items="${contrats}">
            <option value="${ct.id}" ${param.contratId == ct.id ? 'selected' : ''}>
                <c:out value="${ct.numeroContrat}"/> - <c:out value="${ct.typeAssurance.nom}"/>
            </option>
        </c:forEach>
    </select><br><br>

    <label>Date du sinistre</label><br>
    <input type="date" name="date" max="${aujourdhui}"
           value="${fn:escapeXml(param.date)}" required><br><br>

    <label>Type de sinistre</label><br>
    <select name="typeSinistre" required>
        <option value="">-- choisir --</option>
        <c:forEach var="t" items="${['Accident','Vol','Incendie','Dégâts des eaux','Maladie','Autre']}">
            <option ${param.typeSinistre == t ? 'selected' : ''}>${t}</option>
        </c:forEach>
    </select><br><br>

    <label>Montant estimé (€, facultatif)</label><br>
    <input type="number" name="montantEstime" step="0.01" min="0"
           value="${fn:escapeXml(param.montantEstime)}"><br><br>

    <label>Description (10 à 2000 caractères)</label><br>
    <textarea name="description" rows="5" cols="60" minlength="10" maxlength="2000" required><c:out value="${param.description}"/></textarea><br><br>

    <label>Pièces justificatives (PDF, JPG, PNG, 5 Mo max chacune, 5 fichiers max)</label><br>
    <input type="file" name="pieces" multiple accept=".pdf,.jpg,.jpeg,.png"><br><br>

    <button type="submit">Déclarer</button>
</form>
</c:otherwise>
</c:choose>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>