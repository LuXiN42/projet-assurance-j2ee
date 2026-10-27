<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <title>Statistiques — Agent · B'Care</title>
    <%@ include file="/WEB-INF/views/fragments/head.jspf" %>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/nav.jspf" %>
<main class="page">
    <h1>Tableau de bord — Agent</h1>

    <!-- KPI : Clients -->
    <section>
        <h2>Clients</h2>
        <div class="kpis">
            <div class="kpi"><div class="label">Total clients</div><div class="value">${totalClients}</div></div>
            <div class="kpi s"><div class="label">Nouveaux (30 j)</div><div class="value">${newClients30j}</div></div>
            <div class="kpi w"><div class="label">Nouveaux (mois)</div><div class="value">${newClientsMois}</div></div>
        </div>
        <div class="chart-card">
            <h3>Répartition des clients par type d'assurance</h3>
            <canvas id="chartClientsType"></canvas>
        </div>
    </section>

    <!-- KPI : Contrats -->
    <section>
        <h2>Contrats</h2>
        <div class="kpis">
            <div class="kpi s"><div class="label">Actifs</div><div class="value">${cActifs}</div></div>
            <div class="kpi w"><div class="label">En attente</div><div class="value">${cAttente}</div></div>
            <div class="kpi"><div class="label">Suspendus</div><div class="value">${cSuspendu}</div></div>
            <div class="kpi"><div class="label">Expirés</div><div class="value">${cExpires}</div></div>
            <div class="kpi d"><div class="label">Résiliés</div><div class="value">${cResilies}</div></div>
        </div>
        <div class="chart-card">
            <h3>Évolution du nombre de contrats sur 12 mois</h3>
            <canvas id="chartEvolContrats"></canvas>
        </div>
    </section>

    <!-- KPI : Finances -->
    <section>
        <h2>Finances</h2>
        <div class="kpis">
            <div class="kpi s"><div class="label">CA (paiements encaissés)</div>
                <div class="value"><fmt:formatNumber value="${ca}" pattern="#,##0.00"/> €</div></div>
            <div class="kpi w"><div class="label">Indemnisations</div>
                <div class="value"><fmt:formatNumber value="${indemnisations}" pattern="#,##0.00"/> €</div></div>
            <div class="kpi"><div class="label">Nb paiements</div><div class="value">${nbPaiements}</div></div>
        </div>
        <div class="chart-card">
            <h3>Évolution mensuelle (12 mois)</h3>
            <canvas id="chartEvolFin"></canvas>
        </div>
    </section>

</main>

<script>
    // Couleurs et utilitaires
    const palette = ['#1565c0','#00897b','#f57c00','#c62828','#6a1b9a','#43a047','#5d4037'];

    // Clients par type
    new Chart(document.getElementById('chartClientsType'), {
        type: 'pie',
        data: {
            labels: ${clientsParTypeLabelsJson},
            datasets: [{
                data: ${clientsParTypeDataJson},
                backgroundColor: palette
            }]
        }
    });

    // Évolution contrats
    new Chart(document.getElementById('chartEvolContrats'), {
        type: 'line',
        data: {
            labels: ${evolContratsLabelsJson},
            datasets: [{
                label: 'Contrats créés',
                data: ${evolContratsDataJson},
                borderColor: '#1565c0',
                backgroundColor: 'rgba(21,101,192,.15)',
                tension: .3, fill: true
            }]
        },
        options: { scales: { y: { beginAtZero: true, ticks: { precision:0 } } } }
    });

    // Évolution finances (double axe)
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
</script>
<%@ include file="/WEB-INF/views/fragments/pied.jspf" %>
</body>
</html>
