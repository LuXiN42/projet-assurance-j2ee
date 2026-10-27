<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Garanties · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">
<h1>Garanties</h1>
<p><a href="${pageContext.request.contextPath}/agent/garanties/form">+ Nouvelle garantie</a></p>

<c:if test="${not empty message}"><p class="alerte ok"><c:out value="${message}"/></p></c:if>
<c:if test="${not empty erreur}"><p class="alerte ko"><c:out value="${erreur}"/></p></c:if>

<table border="1" cellpadding="4">
  <tr><th>Nom</th><th>Description</th><th>Actions</th></tr>
  <c:forEach var="g" items="${garanties}">
    <tr>
      <td><c:out value="${g.nom}"/></td>
      <td><c:out value="${g.description}"/></td>
      <td>
        <a href="${pageContext.request.contextPath}/agent/garanties/form?id=${g.id}">Modifier</a>
        <form method="post" action="${pageContext.request.contextPath}/agent/garanties/supprimer" style="display:inline">
          <input type="hidden" name="id" value="${g.id}">
          <button type="submit" onclick="return confirm('Supprimer cette garantie ?')">Supprimer</button>
        </form>
      </td>
    </tr>
  </c:forEach>
  <c:if test="${empty garanties}"><tr><td colspan="3">Aucune garantie.</td></tr></c:if>
</table>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>