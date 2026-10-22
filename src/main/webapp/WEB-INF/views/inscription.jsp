<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Inscription</title></head>
<body>
<h1>Créer un compte</h1>

<form method="post" action="${pageContext.request.contextPath}/inscription">
    <label>Nom <input name="nom" value="<c:out value='${param.nom}'/>" required></label>
    <span style="color:red">${erreurs.nom}</span><br>

    <label>Prénom <input name="prenom" value="<c:out value='${param.prenom}'/>" required></label>
    <span style="color:red">${erreurs.prenom}</span><br>

    <label>Email <input type="email" name="email" value="<c:out value='${param.email}'/>" required></label>
    <span style="color:red">${erreurs.email}</span><br>

    <label>Mot de passe <input type="password" name="motDePasse" minlength="8" required></label>
    <span style="color:red">${erreurs.motDePasse}</span><br>

    <label>Confirmation <input type="password" name="confirmation" required></label>
    <span style="color:red">${erreurs.confirmation}</span><br>

    <button type="submit">S'inscrire</button>
</form>
<p><a href="${pageContext.request.contextPath}/login">Déjà inscrit ? Se connecter</a></p>
</body>
</html>