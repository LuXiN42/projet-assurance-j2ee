<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr"><head><meta charset="UTF-8"><title>Contrat ${contrat.numeroContrat}</title></head>
<body>
<p><a href="${ctx}/agent/contrats">← Liste des contrats</a></p>
<h1>Contrat ${contrat.numeroContrat} — <em>${contrat.statut}</em></h1>

<c:if test="${not empty message}"><p style="color:green">${message}</p></c:if>
<c:if test="${not empty erreur}"><p style="color:red">${erreur}</p></c:if>

<table cellpadding="4">
  <tr><th align="left">Client</th><td>${contrat.client.nom} ${contrat.client.prenom} (${contrat.client.email})</td></tr>
  <tr><th align="left">Type</th><td>${contrat.typeAssurance.nom}</td></tr>
  <tr><th align="left">Période</th><td>${contrat.dateDebut} → ${empty contrat.dateFin ? 'indéterminée' : contrat.dateFin}</td></tr>
  <tr><th align="left">Prime</th><td>${contrat.prime} €</td></tr>
</table>

<h2>Garanties</h2>
<ul>
  <c:forEach var="g" items="${contrat.garanties}">
    <li>
      <c:out value="${g.nom}"/>
      <c:if test="${st != 'RESILIE' && st != 'EXPIRE'}">
        <form method="post" action="${ctx}/agent/contrats/garanties" style="display:inline">
          <input type="hidden" name="contratId" value="${contrat.id}">
          <input type="hidden" name="garantieId" value="${g.id}">
          <button name="action" value="dissocier" onclick="return confirm('Retirer cette garantie ?')">Retirer</button>
        </form>
      </c:if>
    </li>
  </c:forEach>
  <c:if test="${empty contrat.garanties}"><li><em>Aucune garantie associée.</em></li></c:if>
</ul>

<c:if test="${st != 'RESILIE' && st != 'EXPIRE' && not empty garantiesDispo}">
  <form method="post" action="${ctx}/agent/contrats/garanties">
    <input type="hidden" name="contratId" value="${contrat.id}">
    <select name="garantieId" required>
      <option value="">— ajouter une garantie —</option>
      <c:forEach var="g" items="${garantiesDispo}">
        <option value="${g.id}"><c:out value="${g.nom}"/></option>
      </c:forEach>
    </select>
    <button name="action" value="associer">Associer</button>
  </form>
</c:if>

<h2>Actions</h2>
<c:set var="st" value="${contrat.statut}"/>
<c:if test="${st != 'RESILIE' && st != 'EXPIRE'}">
  <a href="${ctx}/agent/contrats/form?id=${contrat.id}">Modifier</a>
  |
  <a href="${ctx}/agent/paiements/form?contratId=${contrat.id}">Enregistrer un paiement</a>
  |
</c:if>
<a href="${ctx}/agent/paiements?contratId=${contrat.id}">Voir les paiements</a>

<form method="post" action="${ctx}/agent/contrats/action" style="display:inline">
  <input type="hidden" name="id" value="${contrat.id}">
  <c:if test="${st == 'EN_ATTENTE' || st == 'SUSPENDU'}">
    <button name="action" value="activer">Activer</button>
  </c:if>
  <c:if test="${st == 'ACTIF'}">
    <button name="action" value="suspendre">Suspendre</button>
  </c:if>
  <c:if test="${st == 'ACTIF' || st == 'SUSPENDU'}">
    <button name="action" value="resilier" onclick="return confirm('Résilier définitivement ?')">Résilier</button>
  </c:if>
  <c:if test="${st == 'ACTIF' || st == 'EXPIRE'}">
    <label>Renouveler de <input type="number" name="dureeMois" value="12" min="1" style="width:4em"> mois</label>
    <button name="action" value="renouveler">Renouveler</button>
  </c:if>
</form>
<c:if test="${st == 'RESILIE'}"><p><em>Contrat résilié : aucune action possible.</em></p></c:if>
</body></html>