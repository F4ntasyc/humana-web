<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Jadwal Saya" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="page-header mb-3">
    <h1 class="fw-bold" style="color: #1E293B;">Jadwal Saya</h1>
</div>

<c:if test="${param.sukses == '1'}">
    <div class="alert-custom alert-success-custom"><i class="bi bi-check-circle-fill"></i> Pesanan berhasil dibuat.</div>
</c:if>
<c:if test="${param.batal == '1'}">
    <div class="alert-custom alert-success-custom"><i class="bi bi-check-circle-fill"></i> Pesanan berhasil dibatalkan.</div>
</c:if>
<c:if test="${param.konfirmasi == '1'}">
    <div class="alert-custom alert-success-custom"><i class="bi bi-check-circle-fill"></i> Status pesanan berhasil diupdate.</div>
</c:if>
<c:if test="${param.selesai == '1'}">
    <div class="alert-custom alert-success-custom"><i class="bi bi-check-circle-fill"></i> Sesi ditandai selesai.</div>
</c:if>
<c:if test="${param.bayar == '1'}">
    <div class="alert-custom alert-success-custom"><i class="bi bi-check-circle-fill"></i> Pembayaran berhasil diproses.</div>
</c:if>
<c:if test="${not empty error or not empty param.error}">
    <div class="alert-custom alert-danger-custom"><i class="bi bi-exclamation-circle-fill"></i> ${not empty error ? error : param.error}</div>
</c:if>

<%-- ===== TABS ===== --%>
<style>
    .jadwal-tabs { display: flex; gap: 0; border-bottom: 2px solid #E2E8F0; margin-bottom: 1.5rem; }
    .jadwal-tab-btn {
        padding: 0.75rem 1.5rem; background: none; border: none;
        font-size: 0.95rem; font-weight: 500; color: #64748B;
        border-bottom: 2px solid transparent; margin-bottom: -2px;
        cursor: pointer; transition: all 0.2s; display: flex; align-items: center; gap: 0.5rem;
    }
    .jadwal-tab-btn:hover { color: #1E365C; background: #F8FAFC; }
    .jadwal-tab-btn.active { color: #1E365C; border-bottom-color: #1E365C; font-weight: 700; }
    .jadwal-tab-btn .badge-count {
        background: #E2E8F0; color: #64748B; border-radius: 999px;
        padding: 0.1rem 0.5rem; font-size: 0.75rem; font-weight: 700;
    }
    .jadwal-tab-btn.active .badge-count { background: #1E365C; color: white; }
    .tab-content-panel { display: none; }
    .tab-content-panel.active { display: block; }
</style>

<c:choose>
    <c:when test="${sessionScope.userRole == 'MURID'}">
        <%-- Pisahkan daftar jadwal --%>
        <c:set var="daftarMenunggu" value="${[]}" />
        <c:set var="daftarAktif" value="${[]}" />
        <c:forEach items="${daftarJadwal}" var="j">
            <c:if test="${j.statusPemesanan == 'menunggu konfirmasi'}">
                <c:set var="daftarMenunggu" value="${daftarMenunggu}" /><%-- handled in JS --%>
            </c:if>
        </c:forEach>

        <%-- Tab Nav --%>
        <div class="jadwal-tabs">
            <button class="jadwal-tab-btn ${empty param.tab || param.tab == 'menunggu' ? 'active' : ''}"
                    onclick="switchTab('menunggu', this)">
                <i class="bi bi-hourglass-split"></i> Menunggu
                <span class="badge-count" id="badgeMenunggu">0</span>
            </button>
            <button class="jadwal-tab-btn ${param.tab == 'aktif' ? 'active' : ''}"
                    onclick="switchTab('aktif', this)">
                <i class="bi bi-calendar-check"></i> Jadwal Aktif
                <span class="badge-count" id="badgeAktif">0</span>
            </button>
        </div>

        <%-- Tab: Menunggu (murid) --%>
        <div class="tab-content-panel ${empty param.tab || param.tab == 'menunggu' ? 'active' : ''}" id="panel-menunggu">
        </div>

        <%-- Tab: Aktif (murid) --%>
        <div class="tab-content-panel ${param.tab == 'aktif' ? 'active' : ''}" id="panel-aktif">
        </div>

    </c:when>
    <c:when test="${sessionScope.userRole == 'GURU'}">
        <%-- Tab Nav GURU --%>
        <div class="jadwal-tabs">
            <button class="jadwal-tab-btn ${empty param.tab || param.tab == 'permintaan' ? 'active' : ''}"
                    onclick="switchTab('permintaan', this)">
                <i class="bi bi-inbox"></i> Permintaan
                <span class="badge-count" id="badgePermintaan">0</span>
            </button>
            <button class="jadwal-tab-btn ${param.tab == 'aktif' ? 'active' : ''}"
                    onclick="switchTab('aktif', this)">
                <i class="bi bi-calendar-check"></i> Jadwal Aktif
                <span class="badge-count" id="badgeAktifGuru">0</span>
            </button>
        </div>

        <%-- Tab: Permintaan (guru) --%>
        <div class="tab-content-panel ${empty param.tab || param.tab == 'permintaan' ? 'active' : ''}" id="panel-permintaan">
        </div>

        <%-- Tab: Aktif (guru) --%>
        <div class="tab-content-panel ${param.tab == 'aktif' ? 'active' : ''}" id="panel-aktif">
        </div>
    </c:when>
</c:choose>

<%-- ===== SHARED CARD RENDERING (JS-based filter) ===== --%>
<%-- Semua card dirender via JS dari data JSON --%>

<script id="jadwal-data" type="application/json">${empty daftarJadwalJson ? '[]' : daftarJadwalJson}</script>
<script>
    var jadwalData = [];
    try {
        jadwalData = JSON.parse(document.getElementById('jadwal-data').textContent || '[]');
    } catch (e) {
        console.error('Gagal parse data jadwal:', e);
        jadwalData = [];
    }

    var userRole = '${sessionScope.userRole}';
    var ctx = '${pageContext.request.contextPath}';

    function formatTanggal(dtStr) {
        if (!dtStr || dtStr === 'null') return '-';
        try {
            var d = new Date(dtStr.replace(' ', 'T'));
            return d.toLocaleDateString('id-ID', {day:'2-digit', month:'short', year:'numeric'}) +
                   ', ' + d.toLocaleTimeString('id-ID', {hour:'2-digit', minute:'2-digit', hour12:false});
        } catch(e) { return dtStr; }
    }
    function formatJam(dtStr) {
        if (!dtStr || dtStr === 'null') return '-';
        try {
            var d = new Date(dtStr.replace(' ', 'T'));
            return d.toLocaleTimeString('id-ID', {hour:'2-digit', minute:'2-digit', hour12:false});
        } catch(e) { return dtStr; }
    }

    function renderBadge(status) {
        if (!status) return '';
        var s = status.toLowerCase();
        if (s === 'menunggu konfirmasi') return '<span class="badge bg-warning text-dark rounded-pill">Menunggu</span>';
        if (s === 'dikonfirmasi') return '<span class="badge bg-primary rounded-pill">Dikonfirmasi</span>';
        if (s === 'berlangsung') return '<span class="badge bg-success rounded-pill">Berlangsung</span>';
        return '<span class="badge bg-secondary rounded-pill">' + status + '</span>';
    }

    function renderActions(j) {
        var html = '';
        if (!j.status) return html;
        var s = j.status.toLowerCase();
        
        if (userRole === 'MURID') {
            if (s === 'menunggu konfirmasi') {
                html += '<button type="button" class="btn btn-outline-danger btn-sm rounded-pill px-3" ' +
                        'onclick="showKonfirmasi(\'Batalkan pesanan ini?\', \'Apakah kamu yakin ingin membatalkan pesanan ini?\', function(){ submitForm(\'' + ctx + '/pesan/batal\', {idPemesanan:' + j.idPemesanan + '}) })">' +
                        'Batalkan</button>';
            } else if (s === 'dikonfirmasi') {
                html += '<a href="' + ctx + '/bayar?id=' + j.idPemesanan + '" class="btn btn-primary btn-sm rounded-pill px-4">Bayar</a>';
            } else if (s === 'berlangsung') {
                html += '<span class="badge bg-success rounded-pill px-3 py-2 align-self-center">Sedang Berlangsung</span>';
            }
        } else if (userRole === 'GURU') {
            if (s === 'menunggu konfirmasi') {
                html += '<button type="button" class="btn btn-success btn-sm rounded-pill px-3" ' +
                        'onclick="showKonfirmasi(\'Terima permintaan?\', \'Kamu akan menerima sesi belajar dari murid ini. Lanjutkan?\', function(){ submitForm(\'' + ctx + '/jadwal/konfirmasi\', {idPemesanan:' + j.idPemesanan + ', aksi:\'terima\'}) })">' +
                        'Terima</button> ' +
                        '<button type="button" class="btn btn-danger btn-sm rounded-pill px-3" ' +
                        'onclick="showKonfirmasi(\'Tolak permintaan?\', \'Kamu akan menolak permintaan sesi ini. Tindakan ini tidak bisa dibatalkan.\', function(){ submitForm(\'' + ctx + '/jadwal/konfirmasi\', {idPemesanan:' + j.idPemesanan + ', aksi:\'tolak\'}) }, true)">' +
                        'Tolak</button>';
            } else if (s === 'dikonfirmasi') {
                html += '<button type="button" class="btn btn-primary btn-sm rounded-pill px-3" ' +
                        'onclick="showKonfirmasi(\'Selesaikan sesi?\', \'Tandai sesi ini sebagai selesai? Pastikan sesi sudah benar-benar berlangsung.\', function(){ submitForm(\'' + ctx + '/jadwal/selesai\', {idPemesanan:' + j.idPemesanan + '}) })">' +
                        'Selesaikan Sesi</button>';
            } else if (s === 'berlangsung') {
                html += '<span class="badge bg-success rounded-pill px-3 py-2 align-self-center">Berlangsung</span>';
            }
        }
        return html;
    }

    function renderCard(j) {
        var personLabel = userRole === 'MURID' ? 'Guru' : 'Murid';
        var personName = userRole === 'MURID'
            ? (j.namaGuru || '<span class="text-muted fst-italic">Menunggu guru...</span>')
            : j.namaMurid;

        return '<div class="col-md-6">' +
            '<div class="card shadow-sm border-0 h-100" style="border-radius:1.25rem;">' +
            '<div class="card-body p-4 d-flex flex-column">' +
            '<div class="d-flex justify-content-between align-items-start mb-3">' +
            '<div><h5 class="fw-bold text-dark mb-1">' + j.namaMateri + '</h5>' +
            '<div class="text-secondary" style="font-size:0.85rem;">' + j.namaMapel + '</div></div>' +
            renderBadge(j.status) +
            '</div>' +
            '<div class="mb-3 p-3 rounded" style="background:#F8FAFC;border:1px solid #E2E8F0;font-size:0.9rem;">' +
            '<div class="mb-2"><strong>' + personLabel + ':</strong> ' + personName + '</div>' +
            '<div class="mb-2"><i class="bi bi-clock me-2 text-primary"></i>' +
            formatTanggal(j.waktuMulai) + ' &ndash; ' + formatJam(j.waktuSelesai) + '</div>' +
            '<div><i class="bi bi-geo-alt me-2 text-danger"></i>' + j.lokasiSesi + '</div>' +
            '</div>' +
            '<div class="mt-auto pt-3 border-top d-flex gap-2 justify-content-end">' +
            renderActions(j) +
            '</div></div></div></div>';
    }

    function renderEmptyState(label) {
        return '<div class="col-12"><div class="alert alert-info border-0" style="background:#EFF6FF;color:#1D4ED8;border-radius:0.75rem;">' +
               '<i class="bi bi-info-circle-fill me-2"></i>Belum ada ' + label + '.</div></div>';
    }

    function populatePanels() {
        if (userRole === 'MURID') {
            var badgeMenunggu = document.getElementById('badgeMenunggu');
            var badgeAktif = document.getElementById('badgeAktif');
            var panelMenunggu = document.getElementById('panel-menunggu');
            var panelAktif = document.getElementById('panel-aktif');
            if (!badgeMenunggu || !badgeAktif || !panelMenunggu || !panelAktif) return;
            var menunggu = jadwalData.filter(j => j.status && j.status.toLowerCase() === 'menunggu konfirmasi');
            var aktif = jadwalData.filter(j => j.status && (j.status.toLowerCase() === 'dikonfirmasi' || j.status.toLowerCase() === 'berlangsung'));

            document.getElementById('badgeMenunggu').textContent = menunggu.length;
            document.getElementById('badgeAktif').textContent = aktif.length;

            var pmHtml = '<div class="row g-4">';
            pmHtml += menunggu.length > 0 ? menunggu.map(renderCard).join('') : renderEmptyState('permintaan yang menunggu');
            pmHtml += '</div>';
            document.getElementById('panel-menunggu').innerHTML = pmHtml;

            var paHtml = '<div class="row g-4">';
            paHtml += aktif.length > 0 ? aktif.map(renderCard).join('') : renderEmptyState('jadwal aktif');
            paHtml += '</div>';
            document.getElementById('panel-aktif').innerHTML = paHtml;

        } else if (userRole === 'GURU') {
            var badgePermintaan = document.getElementById('badgePermintaan');
            var badgeAktifGuru = document.getElementById('badgeAktifGuru');
            var panelPermintaan = document.getElementById('panel-permintaan');
            var panelAktifGuru = document.getElementById('panel-aktif');
            if (!badgePermintaan || !badgeAktifGuru || !panelPermintaan || !panelAktifGuru) return;
            var permintaan = jadwalData.filter(j => j.status && j.status.toLowerCase() === 'menunggu konfirmasi');
            var aktifGuru = jadwalData.filter(j => j.status && (j.status.toLowerCase() === 'dikonfirmasi' || j.status.toLowerCase() === 'berlangsung'));

            document.getElementById('badgePermintaan').textContent = permintaan.length;
            document.getElementById('badgeAktifGuru').textContent = aktifGuru.length;

            var prHtml = '<div class="row g-4">';
            prHtml += permintaan.length > 0 ? permintaan.map(renderCard).join('') : renderEmptyState('permintaan masuk');
            prHtml += '</div>';
            document.getElementById('panel-permintaan').innerHTML = prHtml;

            var agHtml = '<div class="row g-4">';
            agHtml += aktifGuru.length > 0 ? aktifGuru.map(renderCard).join('') : renderEmptyState('jadwal aktif');
            agHtml += '</div>';
            document.getElementById('panel-aktif').innerHTML = agHtml;
        }
    }

    function switchTab(tabId, btn) {
        document.querySelectorAll('.jadwal-tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content-panel').forEach(p => p.classList.remove('active'));
        btn.classList.add('active');
        var panel = document.getElementById('panel-' + tabId);
        if (panel) panel.classList.add('active');
    }

    // Modal Konfirmasi Logic
    var confirmActionCallback = null;
    function showKonfirmasi(title, message, callback, isDanger = false) {
        document.getElementById('konfirmasiModalTitle').textContent = title;
        document.getElementById('konfirmasiModalBody').textContent = message;
        
        var btnYa = document.getElementById('btnKonfirmasiYa');
        if (isDanger) {
            btnYa.className = 'btn btn-danger rounded-pill px-4';
        } else {
            btnYa.className = 'btn btn-primary rounded-pill px-4';
        }
        
        confirmActionCallback = callback;
        var modal = new bootstrap.Modal(document.getElementById('konfirmasiModal'));
        modal.show();
    }
    
    document.getElementById('btnKonfirmasiYa').addEventListener('click', function() {
        if (confirmActionCallback) {
            confirmActionCallback();
        }
        var modalEl = document.getElementById('konfirmasiModal');
        var modal = bootstrap.Modal.getInstance(modalEl);
        if (modal) {
            modal.hide();
        }
    });

    // Init
    populatePanels();

    // Handle tab param from URL
    var urlTab = new URLSearchParams(window.location.search).get('tab');
    if (urlTab) {
        var tabBtn = document.querySelector('[onclick*="' + urlTab + '"]');
        if (tabBtn) switchTab(urlTab, tabBtn);
    }
</script>

<!-- Modal Konfirmasi -->
<div class="modal fade" id="konfirmasiModal" tabindex="-1" aria-labelledby="konfirmasiModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content" style="border-radius:1.25rem; border:none; box-shadow: 0 10px 30px rgba(0,0,0,0.1);">
      <div class="modal-header border-0 pb-0">
        <h5 class="modal-title fw-bold" id="konfirmasiModalTitle">Konfirmasi</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body text-secondary" id="konfirmasiModalBody">
        Apakah Anda yakin?
      </div>
      <div class="modal-footer border-0 pt-0">
        <button type="button" class="btn btn-light rounded-pill px-4" data-bs-dismiss="modal">Tidak</button>
        <button type="button" class="btn btn-primary rounded-pill px-4" id="btnKonfirmasiYa">Ya</button>
      </div>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
