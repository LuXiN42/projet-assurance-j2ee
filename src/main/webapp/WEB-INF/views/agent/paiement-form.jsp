<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr"><head><meta charset="UTF-8"><title>Nouveau paiement</title></head>
<body>
<p><a href="${ctx}/agent/paiements">← Paiements</a></p>
<h1>Enregistrer un paiement</h1>

<c:if test="${not empty erreurs}">
  <ul style="color:red"><c:forEach var="e" items="${erreurs}"><li>${e}</li></c:forEach></ul>
</c:if>

<form method="post">
  <p><label>Contrat :
    <select name="contratId" id="contratSel" required>
      <option value="">— choisir —</option>
      <c:forEach var="ct" items="${contrats}">
        <c:set var="sel" value="${param.contratId == ct.id}"/>
        <option value="${ct.id}" data-prime="${ct.prime}" ${sel ? 'selected' : ''}>
          ${ct.numeroContrat} — ${ct.client.nom} ${ct.client.prenom} (${ct.statut})
        </option>
      </c:forEach>
    </select></label></p>

  <p><label>Montant (€) :
    <input type="number" step="0.01" min="0.01" name="montant" id="montant" required
           value="${param.montant}"></label></p>

  <p><label>Date :
    <input type="date" name="date" value="${not empty param.date ? param.date : aujourdhui}"></label></p>

  <p><label>Mode de paiement :
    <select name="mode">
      <option value="">—</option>
      <c:forEach var="m" items="${modes}">
        <option value="${m}" ${param.mode == m ? 'selected' : ''}>${m}</option>
      </c:forEach>
    </select></label></p>

  <p><label>Statut :
    <select name="statut" required>
      <option value="EFFECTUE"  ${param.statut == 'EFFECTUE'  ? 'selected' : ''}>EFFECTUE (payé)</option>
      <option value="EN_ATTENTE" ${param.statut == 'EN_ATTENTE' ? 'selected' : ''}>EN_ATTENTE (échéance à venir)</option>
    </select></label></p>

  <p><button type="submit">Enregistrer</button></p>
</form>

<script>
  // Pré-remplit le montant avec la prime du contrat choisi
  const sel = document.getElementById('contratSel');
  const montant = document.getElementById('montant');
  sel.addEventListener('change', function () {
    const prime = this.selectedOptions[0].dataset.prime;
    if (prime && !montant.value) montant.value = prime;
  });
</script>
</body></html>