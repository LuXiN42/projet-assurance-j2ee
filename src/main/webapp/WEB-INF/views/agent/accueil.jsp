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
        <li><a href="${pageContext.request.contextPath}/agent/contrats">Gérer les contrats</a></li>
        <li>Gérer les sinistres</li>
        <li>Gérer les garanties</li>
        <li><a href="${ctx}/agent/paiements">Paiements</a></li>
        <li><a href="${ctx}/agent/paiements/form">Enregistrer un paiement</a></li>
        <li>Statistiques</li>
    </ul>

    <a href="${pageContext.request.contextPath}/logout">Se déconnecter</a>
</body>
</html>