<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Connexion · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
    <link rel="stylesheet" href="${ctx}/css/public.css">
</head>
<body>
<c:set var="navSombre" value="${true}"/>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>

<main class="page-auth">
    <div class="aurore"><span></span><span></span><span></span></div>

    <div class="boite apparait d1">
        <h1>Bon retour !</h1>
        <p class="sous">Connectez-vous pour retrouver votre espace.</p>

        <c:if test="${param.inscrit == '1'}"><p class="alerte ok">Compte créé, vous pouvez vous connecter.</p></c:if>
        <c:if test="${param.deconnecte == '1'}"><p class="alerte ok">Vous êtes déconnecté.</p></c:if>
        <c:if test="${not empty erreur}"><p class="alerte ko"><c:out value="${erreur}"/></p></c:if>

        <form method="post" action="${ctx}/login">
            <label>Email
                <input type="email" name="email" value="<c:out value='${param.email}'/>" required autofocus>
            </label>
            <label>Mot de passe
                <input type="password" name="motDePasse" required>
            </label>
            <button type="submit">Se connecter</button>
        </form>

        <p class="bas-boite">Pas encore de compte ? <a href="${ctx}/inscription">Créer un compte</a></p>
    </div>
</main>

<script src="${ctx}/js/bcare.js"></script>
</body>
</html>