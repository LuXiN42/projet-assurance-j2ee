<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Utilisateur · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">

<p><a href="${ctx}/admin/utilisateurs">← Retour à la liste</a></p>

<c:choose>
    <c:when test="${empty compte.id}">
        <h1>Créer un utilisateur</h1>
    </c:when>
    <c:otherwise>
        <h1>Modifier l'utilisateur</h1>
    </c:otherwise>
</c:choose>

<c:if test="${not empty erreurs}">
    <ul class="alerte ko">
        <c:forEach var="e" items="${erreurs}">
            <li><c:out value="${e}"/></li>
        </c:forEach>
    </ul>
</c:if>

<form method="post" action="${ctx}/admin/utilisateurs/form">

    <c:if test="${not empty compte.id}">
        <input type="hidden" name="id" value="${compte.id}">
    </c:if>

    <p>
        <label>Nom<br>
            <input type="text" name="nom" maxlength="100" required value="<c:out value='${compte.nom}'/>">
        </label>
    </p>

    <p>
        <label>Prénom<br>
            <input type="text" name="prenom" maxlength="100" required value="<c:out value='${compte.prenom}'/>">
        </label>
    </p>

    <p>
        <label>Email<br>
            <input type="email" name="email" required value="<c:out value='${compte.email}'/>">
        </label>
    </p>

    <p>
        <label>Rôle<br>
            <select name="role" required>
                <c:forEach var="r" items="${roles}">
                    <option value="${r}" <c:if test="${r == compte.role}">selected</c:if>>${r}</option>
                </c:forEach>
            </select>
        </label>
    </p>

    <p>
        <label>Mot de passe
            <c:if test="${not empty compte.id}">(laisser vide pour ne pas le changer)</c:if><br>
            <input type="password" name="motDePasse" minlength="8">
        </label>
    </p>

    <p><button type="submit">Enregistrer</button></p>
</form>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>