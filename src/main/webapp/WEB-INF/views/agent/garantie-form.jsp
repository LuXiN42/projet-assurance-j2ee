<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="edition" value="${not empty garantie and not empty garantie.id}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>${edition ? 'Modifier' : 'Nouvelle'} garantie · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">
<p><a href="${pageContext.request.contextPath}/agent/garanties">← Liste des garanties</a></p>
<h1>${edition ? 'Modifier la garantie' : 'Nouvelle garantie'}</h1>

<c:if test="${not empty erreur}"><p class="alerte ko"><c:out value="${erreur}"/></p></c:if>

<form method="post" action="${pageContext.request.contextPath}/agent/garanties/enregistrer">
  <c:if test="${edition}"><input type="hidden" name="id" value="${garantie.id}"></c:if>
  <p><label>Nom :
    <input name="nom" maxlength="150" required value="<c:out value='${garantie.nom}'/>"></label></p>
  <p><label>Description :<br>
    <textarea name="description" rows="5" cols="50" maxlength="2000"><c:out value="${garantie.description}"/></textarea></label></p>
  <p><button type="submit">Enregistrer</button></p>
</form>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>