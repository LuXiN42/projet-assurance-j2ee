<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Espace client</title></head>
<body>
<h1>Bonjour <c:out value="${sessionScope.utilisateur.prenom}"/> !</h1>
<p><a href="${pageContext.request.contextPath}/logout">Se déconnecter.</a></p>
</body>
</html>