<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Espace client</title>
</head>
<body>
    <h1>Espace client</h1>
    <p>Bonjour ${sessionScope.utilisateur.prenom} !</p>

    <ul>
        <li><a href="${pageContext.request.contextPath}/client/contrats">Mes contrats</a></li>
        <li><a href="${ctx}/client/paiements">Mes paiements</a></li>
    </ul>
<p><a href="${pageContext.request.contextPath}/logout">Se déconnecter.</a></p>
</body>
</html>