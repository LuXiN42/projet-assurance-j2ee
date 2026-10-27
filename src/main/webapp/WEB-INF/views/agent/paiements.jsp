<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr"><head><meta charset="UTF-8"><title>Paiements</title></head>
<body>

<p><a href="${ctx}/agent/accueil">← Accueil agent</a></p>

<h1>Paiements</h1>
<c:if test="${not empty param.msg}"><p style="color:green"><c:out value="${param.msg}"/></p></c:if>

<form method="get">
  Statut :
  <select name="statut" onchange="this.form.submit()">
    <option value="">Tous</option>
    <c:forEach var="s" items="${statuts}">
      <option value="${s}" ${s == statutChoisi ? 'selected' : ''}>${s}</option>
    </c:forEach>
  </select>
  <a href="${ctx}/agent/paiements/form">+ Nouveau paiement</a>
</form>

<table border="1" cellpadding="5">
  <tr><th>Date</th><th>Contrat</th><th>Client</th><th>Montant</th><th>Mode</th><th>Statut</th><th>Actions</th></tr>
  <c:forEach var="p" items="${paiements}">
    <tr>
      <td>${p.date}</td>
      <td><c:out value="${p.contrat.numeroContrat}"/></td>
      <td><c:out value="${p.contrat.client.nom}"/> <c:out value="${p.contrat.client.prenom}"/></td>
      <td>${p.montant} €</td>
      <td>${p.modePaiement}</td>
      <td>${p.statut}</td>
      <td>
        <c:if test="${p.statut == 'EN_ATTENTE'}">
          <form method="post" action="${ctx}/agent/paiements/action" style="display:inline">
            <input type="hidden" name="id" value="${p.id}">
            <select name="mode">
              <c:forEach var="m" items="${modes}"><option><c:out value="${m}"/></option></c:forEach>
            </select>
            <button name="action" value="valider">Valider</button>
            <button name="action" value="echouer">Échec</button>
            <button name="action" value="annuler">Annuler</button>
          </form>
        </c:if>
      </td>
    </tr>
  </c:forEach>
</table>
</body></html>