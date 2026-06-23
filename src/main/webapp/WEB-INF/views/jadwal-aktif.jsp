<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

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

<c:if test="${guruNonaktif}">
    <div class="alert-custom alert-warning-custom">
        <i class="bi bi-pause-circle-fill"></i>
        Anda sedang tidak aktif menerima sesi sekarang. Aktifkan status ketersediaan di halaman Profil untuk melihat permintaan murid.
    </div>
</c:if>

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

<%-- Helper untuk badge status --%>
<c:set var="emptyStateHtml">
    <div class="col-12"><div class="alert alert-info border-0" style="background:#EFF6FF;color:#1D4ED8;border-radius:0.75rem;">
        <i class="bi bi-info-circle-fill me-2"></i>Belum ada data.
    </div></div>
</c:set>

<%-- Calculate counts --%>
<c:set var="countMenunggu" value="0"/>
<c:set var="countAktif" value="0"/>
<c:forEach items="${daftarJadwal}" var="j">
    <c:set var="st" value="${fn:toLowerCase(j.statusPemesanan)}" />
    <c:if test="${st == 'menunggu konfirmasi'}">
        <c:set var="countMenunggu" value="${countMenunggu + 1}"/>
    </c:if>
    <c:if test="${st == 'dikonfirmasi' || st == 'berlangsung'}">
        <c:set var="countAktif" value="${countAktif + 1}"/>
    </c:if>
</c:forEach>

<c:choose>
    <c:when test="${sessionScope.userRole == 'MURID'}">
        <div class="jadwal-tabs">
            <button class="jadwal-tab-btn ${empty param.tab || param.tab == 'menunggu' ? 'active' : ''}"
                    onclick="switchTab('menunggu', this)">
                <i class="bi bi-hourglass-split"></i> Menunggu
                <span class="badge-count">${countMenunggu}</span>
            </button>
            <button class="jadwal-tab-btn ${param.tab == 'aktif' ? 'active' : ''}"
                    onclick="switchTab('aktif', this)">
                <i class="bi bi-calendar-check"></i> Jadwal Aktif
                <span class="badge-count">${countAktif}</span>
            </button>
        </div>

        <%-- Tab: Menunggu (murid) --%>
        <div class="tab-content-panel ${empty param.tab || param.tab == 'menunggu' ? 'active' : ''}" id="panel-menunggu">
            <div class="row g-4">
                <c:choose>
                    <c:when test="${countMenunggu == 0}">${emptyStateHtml}</c:when>
                    <c:otherwise>
                        <c:forEach items="${daftarJadwal}" var="j">
                            <c:if test="${fn:toLowerCase(j.statusPemesanan) == 'menunggu konfirmasi'}">
                                <%@ include file="components/card-jadwal.jsp" %>
                            </c:if>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <%-- Tab: Aktif (murid) --%>
        <div class="tab-content-panel ${param.tab == 'aktif' ? 'active' : ''}" id="panel-aktif">
            <div class="row g-4">
                <c:choose>
                    <c:when test="${countAktif == 0}">${emptyStateHtml}</c:when>
                    <c:otherwise>
                        <c:forEach items="${daftarJadwal}" var="j">
                            <c:if test="${fn:toLowerCase(j.statusPemesanan) == 'dikonfirmasi' || fn:toLowerCase(j.statusPemesanan) == 'berlangsung'}">
                                <%@ include file="components/card-jadwal.jsp" %>
                            </c:if>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:when>

    <c:when test="${sessionScope.userRole == 'GURU'}">
        <div class="jadwal-tabs">
            <button class="jadwal-tab-btn ${empty param.tab || param.tab == 'permintaan' ? 'active' : ''}"
                    onclick="switchTab('permintaan', this)">
                <i class="bi bi-inbox"></i> Permintaan
                <span class="badge-count">${countMenunggu}</span>
            </button>
            <button class="jadwal-tab-btn ${param.tab == 'aktif' ? 'active' : ''}"
                    onclick="switchTab('aktif', this)">
                <i class="bi bi-calendar-check"></i> Jadwal Aktif
                <span class="badge-count">${countAktif}</span>
            </button>
        </div>

        <%-- Tab: Permintaan (guru) --%>
        <div class="tab-content-panel ${empty param.tab || param.tab == 'permintaan' ? 'active' : ''}" id="panel-permintaan">
            <div class="row g-4">
                <c:choose>
                    <c:when test="${countMenunggu == 0}">${emptyStateHtml}</c:when>
                    <c:otherwise>
                        <c:forEach items="${daftarJadwal}" var="j">
                            <c:if test="${fn:toLowerCase(j.statusPemesanan) == 'menunggu konfirmasi'}">
                                <%@ include file="components/card-jadwal.jsp" %>
                            </c:if>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <%-- Tab: Aktif (guru) --%>
        <div class="tab-content-panel ${param.tab == 'aktif' ? 'active' : ''}" id="panel-aktif">
            <div class="row g-4">
                <c:choose>
                    <c:when test="${countAktif == 0}">${emptyStateHtml}</c:when>
                    <c:otherwise>
                        <c:forEach items="${daftarJadwal}" var="j">
                            <c:if test="${fn:toLowerCase(j.statusPemesanan) == 'dikonfirmasi' || fn:toLowerCase(j.statusPemesanan) == 'berlangsung'}">
                                <%@ include file="components/card-jadwal.jsp" %>
                            </c:if>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:when>
</c:choose>

<script>
    function submitForm(actionPath, params) {
        var form = document.createElement('form');
        form.method = 'POST';
        form.action = actionPath;
        for (var key in params) {
            if (params.hasOwnProperty(key)) {
                var hiddenField = document.createElement('input');
                hiddenField.type = 'hidden';
                hiddenField.name = key;
                hiddenField.value = params[key];
                form.appendChild(hiddenField);
            }
        }
        document.body.appendChild(form);
        form.submit();
    }

    function switchTab(tabId, btn) {
        document.querySelectorAll('.jadwal-tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content-panel').forEach(p => p.classList.remove('active'));
        btn.classList.add('active');
        var panel = document.getElementById('panel-' + tabId);
        if (panel) panel.classList.add('active');
    }

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

    document.addEventListener('DOMContentLoaded', function() {
        document.getElementById('btnKonfirmasiYa').addEventListener('click', function() {
            if (confirmActionCallback) confirmActionCallback();
            var modalEl = document.getElementById('konfirmasiModal');
            var modal = bootstrap.Modal.getInstance(modalEl);
            if (modal) modal.hide();
        });

        // Handle tab param from URL
        var urlTab = new URLSearchParams(window.location.search).get('tab');
        if (urlTab) {
            var tabBtn = document.querySelector('[onclick*="' + urlTab + '"]');
            if (tabBtn) switchTab(urlTab, tabBtn);
        }
    });
</script>

<!-- Modal Konfirmasi -->
<div class="modal fade" id="konfirmasiModal" tabindex="-1" aria-labelledby="konfirmasiModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content" style="border-radius:1.25rem; border:none; box-shadow: 0 10px 30px rgba(0,0,0,0.1);">
      <div class="modal-header border-0 pb-0">
        <h5 class="modal-title fw-bold" id="konfirmasiModalTitle">Konfirmasi</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body text-secondary" id="konfirmasiModalBody">Apakah Anda yakin?</div>
      <div class="modal-footer border-0 pt-0">
        <button type="button" class="btn btn-light rounded-pill px-4" data-bs-dismiss="modal">Tidak</button>
        <button type="button" class="btn btn-primary rounded-pill px-4" id="btnKonfirmasiYa">Ya</button>
      </div>
    </div>
  </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
