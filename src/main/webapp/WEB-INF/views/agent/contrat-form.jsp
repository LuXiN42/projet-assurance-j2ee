<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="edition" value="${not empty contrat}"/>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>${edition ? 'Modifier' : 'Nouveau'} contrat · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">
<p><a href="${ctx}/agent/contrats">← Liste des contrats</a></p>
<h1>${edition ? 'Modifier le contrat '.concat(contrat.numeroContrat) : 'Nouveau contrat'}</h1>

<c:if test="${not empty erreurs}">
  <ul class="alerte ko"><c:forEach var="e" items="${erreurs}"><li><c:out value="${e}"/></li></c:forEach></ul>
</c:if>

<form method="post">
  <c:if test="${edition}"><input type="hidden" name="id" value="${contrat.id}"></c:if>

  <c:if test="${not edition}">
    <p><label>N° contrat (laisser vide pour générer) :
      <input name="numeroContrat" value="<c:out value='${param.numeroContrat}'/>"></label></p>
    <p><label>Client :
      <select name="clientId" required>
        <option value="">Choisir</option>
        <c:forEach var="u" items="${clients}">
          <option value="${u.id}" ${param.clientId == u.id ? 'selected' : ''}><c:out value="${u.nom}"/> <c:out value="${u.prenom}"/> - <c:out value="${u.email}"/></option>
        </c:forEach>
      </select></label></p>
  </c:if>

  <p><label>Type d'assurance :
    <select name="typeId" id="typeSelect" required>
        <option value="">Choisir</option>
        <c:forEach var="t" items="${types}">
        <c:set var="sel" value="${not empty param.typeId ? param.typeId == t.id : (edition && contrat.typeAssurance.id == t.id)}"/>
        <option value="${t.id}" data-prix="${t.prixBase}" ${sel ? 'selected' : ''}><c:out value="${t.nom}"/> (${t.prixBase} €)</option>
        </c:forEach>
    </select></label></p>

  <p><label>Date de début :
    <input type="date" name="dateDebut" required
           value="${not empty param.dateDebut ? param.dateDebut : contrat.dateDebut}"></label></p>
  <p><label>Date de fin :
    <input type="date" name="dateFin"
           value="${not empty param.dateFin ? param.dateFin : contrat.dateFin}"></label></p>
  <p><label>Prime (€) :
    <input type="number" step="0.01" min="0.01" name="prime" id="primeInput" required
            value="${not empty param.prime ? param.prime : contrat.prime}"></label></p>

  <fieldset><legend>Garanties</legend>
    <c:forEach var="g" items="${garanties}">
      <label style="display:block">
        <input type="checkbox" name="garantieIds" value="${g.id}" ${selIds.contains(g.id) ? 'checked' : ''}> <c:out value="${g.nom}"/>
      </label>
    </c:forEach>
  </fieldset>

  <p><button type="submit">Enregistrer</button></p>
</form>

<script>
  const typeSelect = document.getElementById('typeSelect');
  const primeInput = document.getElementById('primeInput');

  typeSelect.addEventListener('change', function () {
    const prix = this.selectedOptions[0].dataset.prix;
    if (prix) primeInput.value = prix;
  });
</script>
</main>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>