<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Statistiques — Administration</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
    <style>
        :root { --p:#5e35b1; --s:#00897b; --w:#f57c00; --d:#c62828; --bg:#f4f6f9; }
        body { font-family: Segoe UI, Arial, sans-serif; background: var(--bg); margin:0; color:#222; }
        header { background: var(--p); color:#fff; padding:16px 24px; }
        header h1 { margin:0; font-size:1.4rem; }
        nav a { color:#fff; margin-right:14px; text-decoration:none; }
        main { padding:24px; max-width:1300px; margin:auto; }
        .kpis { display:grid; grid-template-columns: repeat(auto-fit,minmax(200px,1fr)); gap:16px; }
        .kpi { background:#fff; border-radius:10px; padding:18px; box-shadow:0 2px 6px rgba(0,0,0,.08); }
        .kpi .label { color:#666; font-size:.85rem; }
        .kpi .value { font-size:1.8rem; font-weight:700; margin-top:6px; color:var(--p); }
        .kpi.s .value { color:var(--s); } .kpi.w .value { color:var(--w); } .kpi.d .value { color:var(--d); }
        .charts { display:grid; grid-template-columns: repeat(auto-fit,minmax(420px,1fr)); gap:24px; margin-top:28px; }
        .chart-card { background:#fff; border-radius:10px; padding:18px; box-shadow:0 2px 6px rgba(0,0,0,.08); }
        .chart-card h3 { margin:0 0 12px; font-size:1.05rem; }
        table { width:100%; border-collapse:collapse; margin-top:8px; }
        th,td { padding:8px; text-align:left; border-bottom:1px solid #eee; font-size:.9rem; }
        th { background:#fafafa; }
        section { margin-bottom: 28px; }
        .btn-retour {display:inline-block;padding:8px 14px;background:#2c6fbb;color:#fff;text-decoration:none;border-radius:4px;}
        .btn-retour:hover{background:#1f5596;}
    </style>
</head>
<body>
<header>
    <h1>📊 Tableau de bord — Administrateur</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/admin/utilisateurs">Utilisateurs</a>
        <a href="${pageContext.request.contextPath}/admin/types">Types d'assurance</a>
        <a href="${pageContext.request.contextPath}/admin/contrats">Contrats</a>
        <a href="${pageContext.request.contextPath}/admin/sinistres">Sinistres</a>
        <a href="${pageContext.request.contextPath}/admin/statistiques">Statistiques</a>
    </nav>
</header>
<main>

    <!-- Vue d'ensemble utilisateurs -->
    <section>
        <h2 style="margin-top:0">Utilisateurs &amp; Clients</h2>
        <div class="kpis">
            <div class="kpi"><div class="label">Total clients</div><div class="value">${totalClients}</div></div>
            <div class="kpi s"><div class="label">Nouveaux (30 j)</div><div class="value">${newClients30j}</div></div>
            <div class="kpi"><div class="label">Agents</div><div class="value">${nbAgents}</div></div>
            <div class="kpi w"><div class="label">Administrateurs</div><div class="value">${nbAdmins}</div></div>
        </div>
        <div class="chart-card" style="margin-top:16px">
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
        <div class="chart-card" style="margin-top:16px">
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
        <div class="chart-card" style="margin-top:16px">
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
<a href="${pageContext.request.contextPath}/admin/accueil" class="btn-retour">← Retour à l'accueil</a>
</body>
</html>
