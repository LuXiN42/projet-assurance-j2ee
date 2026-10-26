<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Assistant</title>
</head>
<body>

<p><a href="${ctx}/client/accueil">← Accueil</a></p>

<h1>Assistant</h1>
<p><em>Posez une question sur vos contrats, vos garanties ou vos sinistres.</em></p>

<c:if test="${not empty erreur}">
    <p style="color:red"><c:out value="${erreur}"/></p>
</c:if>

<c:if test="${empty historique}">
    <p>Aucun message pour l'instant.</p>
</c:if>

<c:forEach var="m" items="${historique}">
    <c:choose>
        <c:when test="${m[0] == 'user'}">
            <p><strong>Vous :</strong><br>
                <span style="white-space:pre-wrap"><c:out value="${m[1]}"/></span></p>
        </c:when>
        <c:otherwise>
            <p><strong>Assistant :</strong><br>
                <span style="white-space:pre-wrap"><c:out value="${m[1]}"/></span></p>
        </c:otherwise>
    </c:choose>
</c:forEach>

<form method="post" action="${ctx}/client/assistant">
    <p>
        <textarea name="question" rows="3" cols="60" maxlength="500" required></textarea>
    </p>
    <p><button type="submit">Envoyer</button></p>
</form>

<form method="post" action="${ctx}/client/assistant">
    <input type="hidden" name="action" value="effacer">
    <button type="submit">Nouvelle conversation</button>
</form>

</body>
</html>