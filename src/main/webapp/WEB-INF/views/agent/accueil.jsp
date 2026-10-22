<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Espace agent</title>
</head>
<body>
    <h1>Espace agent</h1>
    <p>Bonjour ${sessionScope.utilisateur.prenom} !</p>

    <ul>
        <li>Consulter les clients</li>
        <li>Gérer les contrats</li>
        <li>Gérer les sinistres</li>
        <li>Gérer les garanties</li>
        <li>Enregistrer les paiements</li>
        <li>Statistiques</li>
    </ul>

    <a href="${pageContext.request.contextPath}/logout">Se déconnecter</a>
</body>
</html>