<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Inscription</title></head>
<body>
<h1>Créer un compte</h1>

<c:if test="${not empty erreurs}">
    <ul style="color:red">
        <c:forEach var="e" items="${erreurs}">
            <li><c:out value="${e}"/></li>
        </c:forEach>
    </ul>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/inscription">
    <label>Nom <input name="nom" value="<c:out value='${param.nom}'/>" maxlength="100" required></label><br>

    <label>Prénom <input name="prenom" value="<c:out value='${param.prenom}'/>" maxlength="100" required></label><br>

    <label>Email <input type="email" name="email" value="<c:out value='${param.email}'/>" maxlength="150" required></label><br>

    <label>Mot de passe <input type="password" name="motDePasse" minlength="8" required></label>
    <small>(8 caractères minimum, avec lettres et chiffres)</small><br>

    <label>Confirmation <input type="password" name="confirmation" minlength="8" required></label><br>

    <button type="submit">S'inscrire</button>
</form>
<p><a href="${pageContext.request.contextPath}/login">Déjà inscrit ? Se connecter</a></p>
</body>
</html>