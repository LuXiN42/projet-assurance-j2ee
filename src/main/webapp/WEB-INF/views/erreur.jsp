<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="code" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<c:if test="${empty code}"><c:set var="code" value="500"/></c:if>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Erreur ${code} · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<c:set var="navSombre" value="${true}"/>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>

<c:choose>
    <c:when test="${sessionScope.utilisateur.role == 'ADMIN'}"><c:set var="retour" value="${ctx}/admin/accueil"/></c:when>
    <c:when test="${sessionScope.utilisateur.role == 'AGENT'}"><c:set var="retour" value="${ctx}/agent/accueil"/></c:when>
    <c:when test="${not empty sessionScope.utilisateur}"><c:set var="retour" value="${ctx}/client/accueil"/></c:when>
    <c:otherwise><c:set var="retour" value="${ctx}/"/></c:otherwise>
</c:choose>

<main class="espace-hero page-erreur">
    <div class="aurore"><span></span><span></span><span></span></div>
    <div class="espace-in">
        <div class="code apparait d1">${code}</div>
        <c:choose>
            <c:when test="${code == 404}">
                <h1 class="apparait d2">Cette page n'existe pas.</h1>
                <p class="apparait d3">Le lien est peut-être cassé, ou la page a été déplacée.</p>
            </c:when>
            <c:when test="${code == 403}">
                <h1 class="apparait d2">Vous n'avez pas accès à cette page.</h1>
                <p class="apparait d3">Elle est réservée à un autre type de compte.</p>
            </c:when>
            <c:otherwise>
                <h1 class="apparait d2">Oups, quelque chose s'est mal passé.</h1>
                <p class="apparait d3">Le problème vient de chez nous. Réessayez dans quelques instants.</p>
            </c:otherwise>
        </c:choose>
        <div class="boutons apparait d4">
            <a class="btn btn-blanc btn-grand" href="${retour}">Retour à l'accueil</a>
            <a class="btn btn-leger btn-grand" href="javascript:history.back()">Page précédente</a>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>
