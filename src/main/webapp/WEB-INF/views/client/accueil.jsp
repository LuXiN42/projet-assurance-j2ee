<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Espace client · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<c:set var="navSombre" value="${true}"/>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main>
    <header class="espace-hero">
        <div class="aurore"><span></span><span></span><span></span></div>
        <div class="espace-in">
            <span class="pastille apparait d1"><i></i>Espace client</span>
            <h1 class="apparait d2">Bonjour <c:out value="${sessionScope.utilisateur.prenom}"/>,<br><em>content de vous revoir.</em></h1>
            <p class="apparait d3">Vos contrats, vos sinistres et vos paiements, au même endroit.</p>
            <c:if test="${not empty sessionScope.message}">
                <p class="alerte ok"><c:out value="${sessionScope.message}"/></p>
                <c:remove var="message" scope="session"/>
            </c:if>
        </div>
    </header>

    <div class="tuiles">
        <a class="tuile" href="${ctx}/client/contrats">
            <span class="ico-xl"><svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z"/><path d="M14 3v5h5M9 13h6M9 17h4"/></svg></span>
            <b>Mes contrats</b>
            <small>Vos garanties et vos échéances</small>
        </a>
        <a class="tuile" href="${ctx}/client/sinistres">
            <span class="ico-xl"><svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3l8 3v6c0 4.5-3.4 8.2-8 9-4.6-.8-8-4.5-8-9V6z"/><path d="M9 12l2 2 4-4"/></svg></span>
            <b>Mes sinistres</b>
            <small>Suivez vos dossiers étape par étape</small>
        </a>
        <a class="tuile mise-en-avant" href="${ctx}/client/sinistres/declarer">
            <span class="ico-xl"><svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 8v8M8 12h8"/></svg></span>
            <b>Déclarer un sinistre</b>
            <small>En ligne, en quelques minutes</small>
        </a>
        <a class="tuile" href="${ctx}/client/paiements">
            <span class="ico-xl"><svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 10h18M7 15h3"/></svg></span>
            <b>Mes paiements</b>
            <small>L'historique de vos règlements</small>
        </a>
        <a class="tuile" href="${ctx}/client/assistant">
            <span class="ico-xl"><svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12a8 8 0 0 1-11.6 7.1L4 20l1-4.6A8 8 0 1 1 21 12z"/></svg></span>
            <b>Bibi</b>
            <small>Une question ? Bibi vous répond</small>
        </a>
        <a class="tuile" href="${ctx}/profil">
            <span class="ico-xl"><svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/></svg></span>
            <b>Mon profil</b>
            <small>Vos coordonnées et votre mot de passe</small>
        </a>
    </div>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>
