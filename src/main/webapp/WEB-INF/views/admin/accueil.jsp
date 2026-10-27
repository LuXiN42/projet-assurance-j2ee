<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Espace administrateur</title>
</head>
<body>
    <h1>Espace administrateur</h1>
    <p>Bonjour <c:out value="${sessionScope.utilisateur.prenom}"/> !</p>

       <ul>
        <li><a href="${ctx}/admin/utilisateurs">Gérer les utilisateurs</a></li>
        <li><a href="${ctx}/admin/types">Gérer les types d'assurance</a></li>
        <li><a href="${ctx}/agent/contrats">Tous les contrats</a></li>
        <li><a href="${ctx}/agent/sinistres">Tous les sinistres</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/statistiques">Statistiques globales</a></li>
    </ul>

    <a href="${pageContext.request.contextPath}/logout">Se déconnecter</a>
</body>
</html>