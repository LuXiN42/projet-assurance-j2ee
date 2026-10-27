<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Type d'assurance · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">

<p><a href="${ctx}/admin/types">← Retour à la liste</a></p>

<c:choose>
    <c:when test="${empty type.id}">
        <h1>Ajouter un type d'assurance</h1>
    </c:when>
    <c:otherwise>
        <h1>Modifier le type d'assurance</h1>
    </c:otherwise>
</c:choose>

<c:if test="${not empty erreurs}">
    <ul class="alerte ko">
        <c:forEach var="e" items="${erreurs}">
            <li><c:out value="${e}"/></li>
        </c:forEach>
    </ul>
</c:if>

<form method="post" action="${ctx}/admin/types/form">

    <c:if test="${not empty type.id}">
        <input type="hidden" name="id" value="${type.id}">
    </c:if>

    <p>
        <label>Nom<br>
            <input type="text" name="nom" maxlength="150" required value="<c:out value='${type.nom}'/>">
        </label>
    </p>

    <p>
        <label>Description<br>
            <textarea name="description" rows="4" cols="50" maxlength="2000"><c:out value="${type.description}"/></textarea>
        </label>
    </p>

    <p>
        <label>Prix de base (€)<br>
            <input type="number" name="prixBase" min="0" step="0.01" required value="${type.prixBase}">
        </label>
    </p>

    <p>Garanties disponibles :</p>
    <c:choose>
        <c:when test="${empty garanties}">
            <p><em>Aucune garantie enregistrée pour le moment.</em></p>
        </c:when>
        <c:otherwise>
            <c:forEach var="g" items="${garanties}">
                <label>
                    <input type="checkbox" name="garanties" value="${g.id}"
                        <c:if test="${idsCoches.contains(g.id)}">checked</c:if>>
                    <c:out value="${g.nom}"/>
                </label><br>
            </c:forEach>
        </c:otherwise>
    </c:choose>

    <p><button type="submit">Enregistrer</button></p>
</form>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>