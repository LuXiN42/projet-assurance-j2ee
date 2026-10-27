<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Mes paiements · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">
<h1>Mes paiements</h1>

<h2>Échéances (${jours} prochains jours ou en retard)</h2>
<c:choose>
  <c:when test="${empty echeances}"><p>Aucune échéance.</p></c:when>
  <c:otherwise>
    <table border="1" cellpadding="5">
      <tr><th>Date</th><th>Contrat</th><th>Montant</th></tr>
      <c:forEach var="p" items="${echeances}">
        <tr><td>${p.date}</td><td><c:out value="${p.contrat.numeroContrat}"/></td><td>${p.montant} €</td></tr>
      </c:forEach>
    </table>
  </c:otherwise>
</c:choose>

<h2>Paiements en attente</h2>
<c:choose>
  <c:when test="${empty enAttente}"><p>Aucun paiement en attente.</p></c:when>
  <c:otherwise>
    <table border="1" cellpadding="5">
      <tr><th>Date prévue</th><th>Contrat</th><th>Montant</th></tr>
      <c:forEach var="p" items="${enAttente}">
        <tr><td>${p.date}</td><td><c:out value="${p.contrat.numeroContrat}"/></td><td>${p.montant} €</td></tr>
      </c:forEach>
    </table>
  </c:otherwise>
</c:choose>

<h2>Paiements effectués</h2>
<c:choose>
  <c:when test="${empty effectues}"><p>Aucun paiement effectué.</p></c:when>
  <c:otherwise>
    <table border="1" cellpadding="5">
      <tr><th>Date</th><th>Contrat</th><th>Montant</th><th>Mode</th></tr>
      <c:forEach var="p" items="${effectues}">
        <tr><td>${p.date}</td><td><c:out value="${p.contrat.numeroContrat}"/></td><td>${p.montant} €</td><td>${p.modePaiement}</td></tr>
      </c:forEach>
    </table>
  </c:otherwise>
</c:choose>

<h2>Historique complet</h2>
<table border="1" cellpadding="5">
  <tr><th>Date</th><th>Contrat</th><th>Montant</th><th>Mode</th><th>Statut</th></tr>
  <c:forEach var="p" items="${historique}">
    <tr><td>${p.date}</td><td><c:out value="${p.contrat.numeroContrat}"/></td><td>${p.montant} €</td>
        <td>${p.modePaiement}</td><td>${p.statut}</td></tr>
  </c:forEach>
</table>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>