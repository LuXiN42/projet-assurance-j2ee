<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Inscription · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
    <link rel="stylesheet" href="${ctx}/css/public.css">
</head>
<body>
<c:set var="navSombre" value="${true}"/>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>

<main class="page-auth">
    <div class="aurore"><span></span><span></span><span></span></div>

    <div class="boite apparait d1">
        <h1>Créer un compte</h1>
        <p class="sous">Quelques infos et c'est parti.</p>

        <c:if test="${not empty erreurs}">
            <div class="alerte ko">
                <c:forEach var="e" items="${erreurs}">
                    <div><c:out value="${e}"/></div>
                </c:forEach>
            </div>
        </c:if>

        <form method="post" action="${ctx}/inscription">
            <div class="deux">
                <label>Nom
                    <input name="nom" value="<c:out value='${param.nom}'/>" maxlength="100" required>
                </label>
                <label>Prénom
                    <input name="prenom" value="<c:out value='${param.prenom}'/>" maxlength="100" required>
                </label>
            </div>
            <label>Email
                <input type="email" name="email" value="<c:out value='${param.email}'/>" maxlength="150" required>
            </label>
            <label>Mot de passe
                <input type="password" name="motDePasse" minlength="8" required>
                <small>8 caractères minimum, avec des lettres et des chiffres</small>
            </label>
            <label>Confirmation
                <input type="password" name="confirmation" minlength="8" required>
            </label>
            <button type="submit">Créer mon compte</button>
        </form>

        <p class="bas-boite">Déjà inscrit ? <a href="${ctx}/login">Se connecter</a></p>
    </div>
</main>

<script src="${ctx}/js/bcare.js"></script>
</body>
</html>