<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Détail du sinistre <c:out value="${sinistre.numeroSinistre}"/> · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">

<c:set var="s" value="${sinistre}" />

<c:if test="${not empty sessionScope.message}">
    <div class="alerte ok"><c:out value="${sessionScope.message}"/></div>
    <c:remove var="message" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.erreur}">
    <div class="alerte ko"><c:out value="${sessionScope.erreur}"/></div>
    <c:remove var="erreur" scope="session"/>
</c:if>

<h1>Sinistre <c:out value="${s.numeroSinistre}"/></h1>

<div class="carte">
    <h2>Informations générales</h2>
    <table>
        <tr><th>Numéro</th>      <td><c:out value="${s.numeroSinistre}"/></td></tr>
        <tr><th>Date</th>        <td><c:out value="${s.date}"/></td></tr>
        <tr><th>Statut</th>      <td><c:out value="${s.statut}"/></td></tr>
        <tr><th>Type</th>        <td><c:out value="${s.typeSinistre}"/></td></tr>
        <tr><th>Description</th> <td><c:out value="${s.description}"/></td></tr>
        <tr><th>Montant estimé</th>
            <td><c:out value="${s.montantEstime}"/> €</td></tr>
        <tr><th>Montant indemnisé</th>
            <td><c:choose><c:when test="${empty s.montantIndemnisation}">-</c:when><c:otherwise><c:out value="${s.montantIndemnisation}"/> €</c:otherwise></c:choose></td></tr>
        <tr><th>Contrat</th>
            <td><c:out value="${s.contrat.numeroContrat}"/></td></tr>
        <tr><th>Client</th>
            <td><c:out value="${s.client.nom}"/> <c:out value="${s.client.prenom}"/>
                (<c:out value="${s.client.email}"/>)</td></tr>
        <tr><th>Pièces</th>
            <td>
                <c:choose>
                    <c:when test="${empty s.pieces}">
                        <em>Aucune pièce justificative.</em>
                    </c:when>
                    <c:otherwise>
                        <ul class="pieces">
                            <c:forEach var="p" items="${s.pieces}">
                                <li>
                                    <a href="<c:url value='/piece'>
                                              <c:param name='id' value='${p.id}'/>
                                              </c:url>">
                                        <c:out value="${p.nomOriginal}"/>
                                    </a>
                                    <span class="discret">
                                        (<fmt:formatNumber value="${p.taille}" pattern="#,##0"/> octets,
                                        ${fn:substring(fn:replace(p.dateDepot, 'T', ' '), 0, 16)})
                                    </span>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </td></tr>
    </table>
</div>

<div class="carte">
    <h2>Commentaires (<c:out value="${fn:length(s.commentaires)}"/>)</h2>
    <c:choose>
        <c:when test="${empty s.commentaires}">
            <p>Aucun commentaire pour le moment.</p>
        </c:when>
        <c:otherwise>
            <c:forEach var="c" items="${s.commentaires}">
                <div class="commentaire">
                    <strong><c:out value="${c.auteur.nom}"/> <c:out value="${c.auteur.prenom}"/></strong>
                    <em>(${fn:substring(fn:replace(c.dateCreation, 'T', ' '), 0, 16)})</em>
                    <p><c:out value="${c.texte}"/></p>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

<div class="carte">
    <h2>Ajouter un commentaire</h2>
    <form method="post" action="<c:url value='/agent/sinistre'/>">
        <input type="hidden" name="action" value="ajouterCommentaire"/>
        <input type="hidden" name="sinistreId" value="<c:out value='${s.id}'/>"/>
        <label for="commentaire">Commentaire (max 2000 caractères)</label>
        <textarea id="commentaire" name="commentaire" maxlength="2000" required></textarea>
        <button type="submit">Publier</button>
    </form>
</div>

<div class="carte">
    <h2>Changer le statut</h2>
    <form method="post" action="<c:url value='/agent/sinistre'/>">
        <input type="hidden" name="action" value="changerStatut"/>
        <input type="hidden" name="sinistreId" value="<c:out value='${s.id}'/>"/>
        <label for="statut">Nouveau statut</label>
        <select id="statut" name="statut">
            <c:forEach var="st" items="${statuts}">
                <c:if test="${st != 'INDEMNISE'}">
                    <option value="<c:out value='${st}'/>"
                        <c:if test="${st == s.statut}">selected</c:if>>
                        <c:out value="${st}"/>
                    </option>
                </c:if>
            </c:forEach>
        </select>
        <label for="montant1">Montant d'indemnisation (optionnel ici, sinon utiliser le bouton Indemniser)</label>
        <input id="montant1" type="text" name="montantIndemnisation" placeholder="ex: 1500.00"/>
        <button type="submit">Mettre à jour</button>
    </form>
</div>

<div class="carte">
    <h2>Indemniser le sinistre</h2>
    <form method="post" action="<c:url value='/agent/sinistre'/>"
          onsubmit="return confirm('Confirmer l’indemnisation ?');">
        <input type="hidden" name="action" value="indemniser"/>
        <input type="hidden" name="sinistreId" value="<c:out value='${s.id}'/>"/>
        <label for="montant2">Montant (obligatoire)</label>
        <input id="montant2" type="text" name="montantIndemnisation" required pattern="[0-9]+([.,][0-9]{1,2})?"/>
        <button type="submit">Indemniser</button>
    </form>
    <p><em>Réservé au statut ACCEPTE. Passe automatiquement le sinistre à INDEMNISE.</em></p>
</div>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>