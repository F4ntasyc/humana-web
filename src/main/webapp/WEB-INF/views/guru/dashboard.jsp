<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Dashboard Guru" />
<c:set var="activePage" value="dashboard" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<c:if test="${not empty error}">
    <div class="alert-custom alert-danger-custom"><i class="bi bi-exclamation-circle-fill"></i> ${error}</div>
</c:if>
<c:if test="${guruNonaktif}">
    <div class="alert-custom alert-warning-custom">
        <i class="bi bi-pause-circle-fill"></i>
        Anda sedang tidak aktif menerima sesi. Aktifkan di <a href="${pageContext.request.contextPath}/profil" class="alert-link">Profil</a> untuk melihat permintaan murid.
    </div>
</c:if>

<style>
    .dashboard-banner {
        background-color: var(--humana-navy);
        border-radius: 24px;
        padding: 3rem;
        position: relative;
        overflow: hidden;
    }
    .dashboard-banner::after {
        content: '';
        position: absolute;
        right: -10%;
        top: -30%;
        width: 500px;
        height: 500px;
        background: var(--humana-teal);
        border-radius: 50%;
        opacity: 0.15;
        filter: blur(60px);
        z-index: 1;
        pointer-events: none;
    }
    .greeting {
        position: relative;
        z-index: 10;
    }
    .greeting .title {
        font-size: 2.75rem;
        font-weight: 700;
        color: white;
        letter-spacing: -1px;
        margin-bottom: 0.5rem;
    }
    .greeting .subtitle {
        font-size: 1.1rem;
        color: rgba(255,255,255,0.7);
        max-width: 600px;
        line-height: 1.6;
    }
    
    .section-title { font-size: 1.25rem; font-weight: 700; color: #1E293B; margin-bottom: 1.25rem; display: flex; justify-content: space-between; align-items: center;}
    
    .glass-panel {
        background: white;
        border-radius: 16px;
        padding: 2rem;
        box-shadow: 0 10px 30px rgba(0,0,0,0.04);
        border: 1px solid #E2E8F0;
        position: relative;
        z-index: 10;
    }

    .req-card {
        border-radius: 12px;
        border: 1px solid #E2E8F0;
        padding: 1.5rem;
        transition: all 0.3s;
    }
    .req-card:hover {
        border-color: var(--humana-teal);
        box-shadow: 0 10px 20px rgba(58, 125, 107, 0.08);
    }
    .avatar-circle {
        width: 48px; height: 48px;
        background: var(--humana-navy);
        color: white;
        border-radius: 50%;
        display: flex; align-items: center; justify-content: center;
        font-weight: bold; font-size: 1.1rem;
    }
    
    .shortcut-item {
        display: flex; align-items: center; gap: 1.25rem;
        padding: 1.5rem;
        border: 1px solid #E2E8F0;
        border-radius: 16px;
        margin-bottom: 1.25rem;
        text-decoration: none; color: #1E293B;
        transition: all 0.3s;
        background: white;
        box-shadow: 0 4px 15px rgba(0,0,0,0.02);
    }
    .shortcut-item:hover {
        border-color: var(--humana-teal);
        box-shadow: 0 10px 25px rgba(0,0,0,0.06);
        transform: translateY(-3px);
    }
    .shortcut-item-icon {
        width: 56px; height: 56px;
        background: #F1F5F9; color: #64748B;
        border-radius: 12px;
        display: flex; align-items: center; justify-content: center;
        font-size: 1.5rem;
        transition: all 0.3s;
    }
    .shortcut-item:hover .shortcut-item-icon {
        background: var(--humana-teal); color: white;
        box-shadow: 0 8px 20px rgba(58, 125, 107, 0.3);
    }
    
    .stat-box {
        background: white;
        border: 1px solid #E2E8F0;
        border-radius: 16px;
        padding: 1.5rem;
        box-shadow: 0 4px 15px rgba(0,0,0,0.02);
    }
    .stat-label { font-size: 0.75rem; font-weight: 700; color: #64748B; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 0.5rem; }
    .stat-value { font-size: 1.75rem; font-weight: 700; color: #1E293B; }

    .hero-stat-grid {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 1rem;
        margin-top: 1.5rem;
        position: relative;
        z-index: 10;
    }
    .hero-stat-card {
        background: rgba(255, 255, 255, 0.97);
        backdrop-filter: blur(12px);
        border: 1px solid rgba(255, 255, 255, 0.6);
        border-radius: 16px;
        padding: 1.25rem 1.35rem;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
        display: flex;
        align-items: flex-start;
        gap: 1rem;
        transition: transform 0.2s ease, box-shadow 0.2s ease;
    }
    .hero-stat-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
    }
    .hero-stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.25rem;
        flex-shrink: 0;
    }
    .hero-stat-icon.rating { background: #FEF3C7; color: #D97706; }
    .hero-stat-icon.sesi { background: #DBEAFE; color: #2563EB; }
    .hero-stat-icon.request { background: #E0E7FF; color: #4F46E5; }
    .hero-stat-label {
        font-size: 0.68rem;
        font-weight: 700;
        text-transform: uppercase;
        letter-spacing: 0.8px;
        color: #64748B;
        margin-bottom: 0.35rem;
    }
    .hero-stat-value {
        font-size: 1.65rem;
        font-weight: 700;
        color: #1E293B;
        line-height: 1.2;
    }
    .hero-stat-value .unit {
        font-size: 0.85rem;
        font-weight: 500;
        color: #94A3B8;
    }
    .hero-stat-sub {
        font-size: 0.75rem;
        color: #94A3B8;
        margin-top: 0.2rem;
    }
    @media (max-width: 991.98px) {
        .hero-stat-grid { grid-template-columns: 1fr; }
        .dashboard-banner { padding: 2rem 1.5rem; }
        .greeting .title { font-size: 2rem; }
    }
</style>

<div class="dashboard-banner mb-4">
    <div class="greeting">
        <div class="title">Halo, ${sessionScope.userName}!</div>
        <div class="subtitle mt-2">Kelola permintaan murid dan pantau sesi belajar Anda dari sini.</div>
        <div class="hero-stat-grid">
            <div class="hero-stat-card">
                <div class="hero-stat-icon rating"><i class="bi bi-star-fill"></i></div>
                <div>
                    <div class="hero-stat-label">Kepuasan Siswa</div>
                    <div class="hero-stat-value">
                        <fmt:formatNumber value="${rating}" maxFractionDigits="1" minFractionDigits="1"/>/5.0
                    </div>
                    <div class="hero-stat-sub">Rata-rata rating dari murid</div>
                </div>
            </div>
            <div class="hero-stat-card">
                <div class="hero-stat-icon sesi"><i class="bi bi-calendar-check"></i></div>
                <div>
                    <div class="hero-stat-label">Sesi Aktif</div>
                    <div class="hero-stat-value">${sesiAktif} <span class="unit">sesi</span></div>
                    <div class="hero-stat-sub">Sedang berlangsung atau dikonfirmasi</div>
                </div>
            </div>
            <div class="hero-stat-card">
                <div class="hero-stat-icon request"><i class="bi bi-inbox"></i></div>
                <div>
                    <div class="hero-stat-label">Permintaan Masuk</div>
                    <div class="hero-stat-value">${permintaanMasuk}</div>
                    <div class="hero-stat-sub">Menunggu konfirmasi Anda</div>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="row g-4 position-relative" style="z-index: 10;">
    <!-- Kiri: Permintaan Terbaru -->
    <div class="col-lg-8">
        <div class="glass-panel h-100">
            <div class="section-title">
                Permintaan Baru
                <a href="${pageContext.request.contextPath}/jadwal?tab=permintaan" class="fs-6 fw-semibold text-decoration-none" style="color: var(--humana-teal);">Lihat Semua</a>
            </div>
            
            <div class="row g-3">
                <c:choose>
                    <c:when test="${empty permintaanTerbaru}">
                        <div class="col-12 text-center py-5">
                            <i class="bi bi-inbox fs-1 text-muted mb-3 d-block"></i>
                            <h5 class="fw-bold text-dark mb-1">Tidak Ada Permintaan</h5>
                            <p class="text-secondary small">Belum ada murid yang memesan sesi saat ini.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${permintaanTerbaru}" var="p">
                            <div class="col-md-6">
                                <div class="req-card h-100 d-flex flex-column bg-white">
                                    <div class="d-flex align-items-center gap-3 mb-4">
                                        <div class="avatar-circle">${p.namaMurid.substring(0,2).toUpperCase()}</div>
                                        <div>
                                            <div class="fw-bold text-dark" style="font-size: 1.1rem; line-height: 1.2;">${p.namaMurid}</div>
                                            <div class="text-secondary small fw-medium mt-1">${p.namaMateri}</div>
                                        </div>
                                    </div>
                                    <div class="row g-0 mb-4 mt-auto">
                                        <div class="col-7">
                                            <div class="text-secondary" style="font-size: 0.7rem; font-weight: 700; letter-spacing: 0.5px;">WAKTU</div>
                                            <div class="fw-bold text-dark small"><fmt:formatDate value="${p.waktuMulai}" pattern="dd MMM yyyy, HH:mm" /></div>
                                        </div>
                                        <div class="col-5 border-start ps-3">
                                            <div class="text-secondary" style="font-size: 0.7rem; font-weight: 700; letter-spacing: 0.5px;">STATUS</div>
                                            <div class="fw-bold text-warning small">Menunggu</div>
                                        </div>
                                    </div>
                                    <a href="${pageContext.request.contextPath}/jadwal?tab=permintaan" class="btn btn-dark w-100 rounded-pill fw-semibold" style="background: var(--humana-navy); border: none;">Lihat Detail</a>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    
    <!-- Kanan: Shortcuts -->
    <div class="col-lg-4">
        <a href="${pageContext.request.contextPath}/guru/pendapatan" class="shortcut-item mt-2 mt-lg-0">
            <div class="shortcut-item-icon"><i class="bi bi-wallet2"></i></div>
            <div>
                <div class="fw-bold fs-5 mb-1">Pendapatan</div>
                <div class="small text-secondary">Lihat ringkasan pendapatan</div>
            </div>
        </a>
        <a href="${pageContext.request.contextPath}/jadwal" class="shortcut-item">
            <div class="shortcut-item-icon"><i class="bi bi-calendar2-week"></i></div>
            <div>
                <div class="fw-bold fs-5 mb-1">Jadwal Saya</div>
                <div class="small text-secondary">Kelola jadwal mengajar</div>
            </div>
        </a>
        <a href="${pageContext.request.contextPath}/materi" class="shortcut-item">
            <div class="shortcut-item-icon"><i class="bi bi-journal-bookmark"></i></div>
            <div>
                <div class="fw-bold fs-5 mb-1">Materi</div>
                <div class="small text-secondary">Akses modul materi</div>
            </div>
        </a>
        <a href="${pageContext.request.contextPath}/histori" class="shortcut-item mb-0">
            <div class="shortcut-item-icon"><i class="bi bi-clock-history"></i></div>
            <div>
                <div class="fw-bold fs-5 mb-1">Riwayat Sesi</div>
                <div class="small text-secondary">Lihat histori sesi lalu</div>
            </div>
        </a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
