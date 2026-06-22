<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Dashboard Guru" />
<c:set var="activePage" value="dashboard" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

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
</style>

<div class="dashboard-banner mb-4">
    <div class="greeting">
        <div class="title">Halo, ${sessionScope.userName}!</div>
        <div class="row g-3 mt-3" style="position:relative; z-index:10;">
            <div class="col-4">
                <div style="background:white; border:none; border-radius:12px; padding:1rem 1.25rem; box-shadow: 0 4px 15px rgba(0,0,0,0.05);">
                    <div style="font-size:0.65rem; font-weight:700; text-transform:uppercase; letter-spacing:1px; color:#64748B; margin-bottom:0.4rem;">KEPUASAN SISWA</div>
                    <div class="d-flex align-items-center gap-2">
                        <span style="font-size:1.6rem; font-weight:700; color:#1E293B;"><fmt:formatNumber value="${rating}" maxFractionDigits="1" minFractionDigits="1"/>/5.0</span>
                        <i class="bi bi-star-fill" style="color:#FBBF24; font-size:1.1rem;"></i>
                    </div>
                </div>
            </div>
            <div class="col-4">
                <div style="background:white; border:none; border-radius:12px; padding:1rem 1.25rem; box-shadow: 0 4px 15px rgba(0,0,0,0.05);">
                    <div style="font-size:0.65rem; font-weight:700; text-transform:uppercase; letter-spacing:1px; color:#64748B; margin-bottom:0.4rem;">SESI AKTIF</div>
                    <div style="font-size:1.6rem; font-weight:700; color:#1E293B;">${sesiAktif} <span style="font-size:0.9rem; font-weight:400; color:#64748B;">sesi</span></div>
                </div>
            </div>
            <div class="col-4">
                <div style="background:white; border:none; border-radius:12px; padding:1rem 1.25rem; box-shadow: 0 4px 15px rgba(0,0,0,0.05);">
                    <div style="font-size:0.65rem; font-weight:700; text-transform:uppercase; letter-spacing:1px; color:#64748B; margin-bottom:0.4rem;">PERMINTAAN MASUK</div>
                    <div style="font-size:1.6rem; font-weight:700; color:#1E293B;">${permintaanMasuk}</div>
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
        <a href="${pageContext.request.contextPath}/jadwal" class="shortcut-item mt-2 mt-lg-0">
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
