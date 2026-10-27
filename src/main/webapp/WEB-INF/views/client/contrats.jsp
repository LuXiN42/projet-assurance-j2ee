<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Mes contrats · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">

<h1>Mes contrats</h1>

<table border="1" cellpadding="4">
  <tr><th>N°</th><th>Type</th><th>Début</th><th>Fin</th><th>Prime</th><th>Statut</th><th></th></tr>
  <c:forEach var="ct" items="${contrats}">
    <tr>
      <td><c:out value="${ct.numeroContrat}"/></td>
      <td><c:out value="${ct.typeAssurance.nom}"/></td>
      <td>${ct.dateDebut}</td>
      <td>${empty ct.dateFin ? '-' : ct.dateFin}</td>
      <td>${ct.prime} €</td>
      <td>${ct.statut}</td>
      <td><a href="${ctx}/client/contrats/detail?id=${ct.id}">Voir</a></td>
    </tr>
  </c:forEach>
  <c:if test="${empty contrats}">
    <tr><td colspan="7">Vous n'avez aucun contrat pour le moment.</td></tr>
  </c:if>
</table>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>