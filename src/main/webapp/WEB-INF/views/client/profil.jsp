<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mon profil</title>
</head>
<body>

<p><a href="${ctx}/client/accueil">← Accueil</a></p>

<h1>Mon profil</h1>

<c:if test="${not empty message}">
    <p style="color:green"><c:out value="${message}"/></p>
</c:if>

<c:if test="${not empty erreurs}">
    <ul style="color:red">
        <c:forEach var="e" items="${erreurs}">
            <li><c:out value="${e}"/></li>
        </c:forEach>
    </ul>
</c:if>

<form method="post" action="${ctx}/client/profil">

    <h2>Mes informations</h2>

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
        <label>Téléphone<br>
            <input type="tel" name="telephone" maxlength="15" value="<c:out value='${compte.telephone}'/>">
        </label>
    </p>

    <p>
        <label>Adresse<br>
            <input type="text" name="adresse" maxlength="255" size="50" value="<c:out value='${compte.adresse}'/>">
        </label>
    </p>

    <h2>Changer de mot de passe</h2>
    <p><em>Laissez ces champs vides pour garder votre mot de passe actuel.</em></p>

    <p>
        <label>Mot de passe actuel<br>
            <input type="password" name="ancienMotDePasse">
        </label>
    </p>

    <p>
        <label>Nouveau mot de passe (8 caractères minimum, avec lettres et chiffres)<br>
            <input type="password" name="nouveauMotDePasse" minlength="8">
        </label>
    </p>

    <p>
        <label>Confirmer le nouveau mot de passe<br>
            <input type="password" name="confirmation">
        </label>
    </p>

    <p><button type="submit">Enregistrer</button></p>
</form>

</body>
</html>