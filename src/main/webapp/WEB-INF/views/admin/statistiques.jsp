<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Statistiques — Administration · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">
    <h1>Tableau de bord — Administrateur</h1>

    <section class="chart-card">
        <h2>Analyse par l'IA</h2>
        <c:if test="${not empty erreurIA}">
            <p class="alerte ko"><c:out value="${erreurIA}"/></p>
        </c:if>
        <c:if test="${not empty analyseIA}">
            <p class="texte-ia"><c:out value="${analyseIA}"/></p>
        </c:if>
        <form method="post" action="${pageContext.request.contextPath}/admin/statistiques">
            <button type="submit">Analyser les chiffres avec l'IA</button>
        </form>
    </section>

    <!-- Vue d'ensemble utilisateurs -->
    <section>
        <h2>Utilisateurs &amp; Clients</h2>
        <div class="kpis">
            <div class="kpi"><div class="label">Total clients</div><div class="value">${totalClients}</div></div>
            <div class="kpi s"><div class="label">Nouveaux (30 j)</div><div class="value">${newClients30j}</div></div>
            <div class="kpi"><div class="label">Agents</div><div class="value">${nbAgents}</div></div>
            <div class="kpi w"><div class="label">Administrateurs</div><div class="value">${nbAdmins}</div></div>
        </div>
        <div class="chart-card">
            <h3>Répartition clients par type d'assurance</h3>
            <canvas id="chartClientsType"></canvas>
        </div>
    </section>

    <!-- Contrats -->
    <section>
        <h2>Contrats</h2>
        <div class="kpis">
            <div class="kpi s"><div class="label">Actifs</div><div class="value">${cActifs}</div></div>
            <div class="kpi w"><div class="label">En attente</div><div class="value">${cAttente}</div></div>
            <div class="kpi"><div class="label">Suspendus</div><div class="value">${cSuspendu}</div></div>
            <div class="kpi"><div class="label">Expirés</div><div class="value">${cExpires}</div></div>
            <div class="kpi d"><div class="label">Résiliés</div><div class="value">${cResilies}</div></div>
            <div class="kpi"><div class="label">Prime moyenne</div>
                <div class="value"><fmt:formatNumber value="${primeMoyenne}" pattern="#,##0.00"/> €</div></div>
        </div>
        <div class="chart-card">
            <h3>Évolution du nombre de contrats (12 mois)</h3>
            <canvas id="chartEvolContrats"></canvas>
        </div>
    </section>

    <!-- Finances -->
    <section>
        <h2>Finances</h2>
        <div class="kpis">
            <div class="kpi s"><div class="label">CA (encaissé)</div>
                <div class="value"><fmt:formatNumber value="${ca}" pattern="#,##0.00"/> €</div></div>
            <div class="kpi w"><div class="label">Indemnisations</div>
                <div class="value"><fmt:formatNumber value="${indemnisations}" pattern="#,##0.00"/> €</div></div>
            <div class="kpi d"><div class="label">Taux de sinistralité</div>
                <div class="value"><fmt:formatNumber value="${tauxSinistralite}" pattern="#,##0.00"/> %</div></div>
            <div class="kpi"><div class="label">Paiements en attente</div><div class="value">${nbPaiementsEnAttente}</div></div>
        </div>
        <div class="chart-card">
            <h3>Évolution mensuelle Paiements / Indemnisations (12 mois)</h3>
            <canvas id="chartEvolFin"></canvas>
        </div>
    </section>

    <!-- Sinistres par statut -->
    <section>
        <h2>Sinistres</h2>
        <div class="kpis">
            <c:forEach var="s" items="${sinistresParStatut}">
                <div class="kpi"><div class="label">${s.statut}</div><div class="value">${s.total}</div></div>
            </c:forEach>
        </div>
        <div class="charts">
            <div class="chart-card">
                <h3>Répartition des sinistres par statut</h3>
                <canvas id="chartSinistresStatut"></canvas>
            </div>
            <div class="chart-card">
                <h3>Top 5 contrats les plus sinistrés</h3>
                <table>
                    <thead><tr><th>Contrat</th><th>Nb sinistres</th></tr></thead>
                    <tbody>
                    <c:forEach var="t" items="${topContratsSinistres}">
                        <tr><td>#${t.contratId}</td><td>${t.total}</td></tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </section>

</main>

<script>
    const palette = ['#5e35b1','#00897b','#f57c00','#c62828','#1565c0','#43a047','#6a1b9a'];

    new Chart(document.getElementById('chartClientsType'), {
        type: 'pie',
        data: {
            labels: ${clientsParTypeLabelsJson},
            datasets: [{ data: ${clientsParTypeDataJson}, backgroundColor: palette }]
        }
    });

    new Chart(document.getElementById('chartEvolContrats'), {
        type: 'line',
        data: {
            labels: ${evolContratsLabelsJson},
            datasets: [{
                label: 'Contrats créés',
                data: ${evolContratsDataJson},
                borderColor: '#5e35b1', backgroundColor: 'rgba(94,53,177,.15)',
                tension: .3, fill: true
            }]
        },
        options: { scales: { y: { beginAtZero: true, ticks: { precision:0 } } } }
    });

    new Chart(document.getElementById('chartEvolFin'), {
        type: 'bar',
        data: {
            labels: ${evolFinLabelsJson},
            datasets: [
                { label: 'Paiements (€)', data: ${evolFinPaiementsJson}, backgroundColor: '#00897b' },
                { label: 'Indemnisations (€)', data: ${evolFinIndemJson}, backgroundColor: '#c62828' }
            ]
        },
        options: { scales: { y: { beginAtZero: true } } }
    });

    new Chart(document.getElementById('chartSinistresStatut'), {
        type: 'doughnut',
        data: {
            labels: ${sinistresStatutLabelsJson},
            datasets: [{ data: ${sinistresStatutDataJson}, backgroundColor: palette }]
        }
    });
</script>

<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>