<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Espace client</title>
</head>
<body>
    <h1>Espace client</h1>
    <p>Bonjour <c:out value="${sessionScope.utilisateur.prenom}"/> !</p>

    <c:if test="${not empty sessionScope.message}">
        <p style="color:green"><c:out value="${sessionScope.message}"/></p>
        <c:remove var="message" scope="session"/>
    </c:if>

    <ul>
        <li><a href="${ctx}/client/contrats">Mes contrats</a></li>
        <li><a href="${ctx}/client/paiements">Mes paiements</a></li>
        <li><a href="${ctx}/client/sinistres">Mes sinistres</a></li>
        <li><a href="${ctx}/client/sinistres/declarer">Déclarer un sinistre</a></li>
    </ul>

    <p><a href="${ctx}/logout">Se déconnecter</a></p>
</body>
</html>