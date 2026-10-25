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
        <li><a href="${ctx}/admin/utilisateurs">Gérer les utilisateurs</a></li>
        <li><a href="${ctx}/admin/types">Gérer les types d'assurance</a></li>
        <li><a href="${ctx}/agent/contrats">Tous les contrats</a></li>
        <li><a href="${ctx}/agent/sinistres">Tous les sinistres</a></li>
        <li>Statistiques globales</li>
    </ul>

    <a href="${pageContext.request.contextPath}/logout">Se déconnecter</a>
</body>
</html>