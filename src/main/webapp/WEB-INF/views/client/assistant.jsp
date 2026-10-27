<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Bibi · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">

<h1>Bibi, votre assistant</h1>
<p>Posez une question sur vos contrats, vos garanties ou vos sinistres. Bibi connaît votre dossier et vous répond à toute heure.</p>

<c:set var="bibiClasse" value="bibi-page"/>
<%@ include file="/WEB-INF/views/fragments/bibi.jspf" %>

</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>
