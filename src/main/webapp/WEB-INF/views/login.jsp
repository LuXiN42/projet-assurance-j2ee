<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Connexion</title></head>
<body>
<h1>Connexion</h1>

<c:if test="${param.inscrit == '1'}"><p style="color:green">Compte créé, vous pouvez vous connecter.</p></c:if>
<c:if test="${param.deconnecte == '1'}"><p style="color:green">Vous êtes déconnecté.</p></c:if>
<c:if test="${not empty erreur}"><p style="color:red">${erreur}</p></c:if>

<form method="post" action="${pageContext.request.contextPath}/login">
    <label>Email <input type="email" name="email" value="<c:out value='${param.email}'/>" required></label><br>
    <label>Mot de passe <input type="password" name="motDePasse" required></label><br>
    <button type="submit">Se connecter</button>
</form>
<p><a href="${pageContext.request.contextPath}/inscription">Créer un compte</a></p>
</body>
</html>