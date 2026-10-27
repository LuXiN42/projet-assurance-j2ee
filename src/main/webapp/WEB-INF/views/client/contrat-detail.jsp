<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Contrat <c:out value="${contrat.numeroContrat}"/> · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">
<p><a href="${ctx}/client/contrats">← Mes contrats</a></p>
<h1>Contrat <c:out value="${contrat.numeroContrat}"/> — <em>${contrat.statut}</em></h1>

<table cellpadding="4">
  <tr><th align="left">Type d'assurance</th><td><c:out value="${contrat.typeAssurance.nom}"/></td></tr>
  <tr><th align="left">Période</th>
      <td>${contrat.dateDebut} → ${empty contrat.dateFin ? 'indéterminée' : contrat.dateFin}</td></tr>
  <tr><th align="left">Prime</th><td>${contrat.prime} €</td></tr>
</table>

<h2>Mes garanties</h2>
<ul>
  <c:forEach var="g" items="${contrat.garanties}"><li><c:out value="${g.nom}"/></li></c:forEach>
  <c:if test="${empty contrat.garanties}"><li><em>Aucune garantie associée.</em></li></c:if>
</ul>

<c:if test="${contrat.statut == 'ACTIF'}">
  <p><a href="${ctx}/client/sinistres/declarer?contratId=${contrat.id}">Déclarer un sinistre sur ce contrat</a></p>
</c:if>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>