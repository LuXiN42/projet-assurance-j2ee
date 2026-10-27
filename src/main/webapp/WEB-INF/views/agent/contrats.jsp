<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Contrats · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">
<h1>Contrats</h1>
<p><a href="${pageContext.request.contextPath}/agent/contrats/form">+ Nouveau contrat</a></p>

<c:if test="${not empty message}"><p class="alerte ok"><c:out value="${message}"/></p></c:if>

<c:if test="${not empty clientFiltre}">
  <p style="background:#eef;padding:6px">
    Contrats de <strong><c:out value="${clientFiltre.prenom} ${clientFiltre.nom}"/></strong>
    - <a href="${pageContext.request.contextPath}/agent/contrats">Voir tous les contrats</a>
  </p>
</c:if>

<form method="get" action="${pageContext.request.contextPath}/agent/contrats">
  <input name="numero" placeholder="N° contrat" value="<c:out value='${param.numero}'/>">
  <input name="nomClient" placeholder="Nom client" value="<c:out value='${param.nomClient}'/>">
  <select name="typeId">
    <option value="">Tous les types</option>
    <c:forEach var="t" items="${types}">
      <option value="${t.id}" ${param.typeId == t.id ? 'selected' : ''}><c:out value="${t.nom}"/></option>
    </c:forEach>
  </select>
  <select name="statut">
    <option value="">Tous les statuts</option>
    <c:forEach var="s" items="${statuts}">
      <option value="${s}" ${param.statut == s ? 'selected' : ''}>${s}</option>
    </c:forEach>
  </select>
  <button type="submit">Rechercher</button>
</form>

<table border="1" cellpadding="4">
  <tr><th>N°</th><th>Client</th><th>Type</th><th>Début</th><th>Fin</th><th>Prime</th><th>Statut</th><th></th></tr>
  <c:forEach var="ct" items="${contrats}">
    <tr>
      <td><c:out value="${ct.numeroContrat}"/></td>
      <td><c:out value="${ct.client.nom}"/> <c:out value="${ct.client.prenom}"/></td>
      <td><c:out value="${ct.typeAssurance.nom}"/></td>
      <td>${ct.dateDebut}</td><td>${ct.dateFin}</td>
      <td>${ct.prime} €</td>
      <td>${ct.statut}</td>
      <td><a href="${pageContext.request.contextPath}/agent/contrats/detail?id=${ct.id}">Détail</a></td>
    </tr>
  </c:forEach>
  <c:if test="${empty contrats}"><tr><td colspan="8">Aucun contrat.</td></tr></c:if>
</table>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>