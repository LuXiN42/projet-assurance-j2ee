<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Espace administrateur</title>
</head>
<body>
    <h1>Espace administrateur</h1>
    <p>Bonjour ${sessionScope.utilisateur.prenom} !</p>

    <ul>
        <li>Gérer les utilisateurs</li>
        <li>Gérer les types d'assurance</li>
        <li>Tous les contrats</li>
        <li>Tous les sinistres</li>
        <li>Statistiques globales</li>
    </ul>

    <a href="${pageContext.request.contextPath}/logout">Se déconnecter</a>
</body>
</html>