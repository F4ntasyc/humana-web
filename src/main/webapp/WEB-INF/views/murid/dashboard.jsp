<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Dashboard Murid" />
<c:set var="activePage" value="dashboard" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<style>
    .dashboard-banner {
        background-color: var(--humana-navy);
        border-radius: 24px;
        padding: 3rem 3rem 10rem 3rem;
        position: relative;
        margin-bottom: 7rem;
        overflow: visible; /* Changed from hidden so the banner card can pop out */
    }
    .dashboard-banner::before {
        content: '';
        position: absolute;
        inset: 0;
        background-color: var(--humana-navy);
        border-radius: 24px;
        overflow: hidden;
        z-index: 1;
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
        z-index: 2;
        pointer-events: none;
    }
    .greeting {
        position: relative;
        z-index: 10;
    }
    .greeting .subtitle {
        font-size: 1.1rem;
        color: rgba(255,255,255,0.7);
        margin-bottom: 0.25rem;
    }
    .greeting .title {
        font-size: 2.75rem;
        font-weight: 700;
        color: white;
        letter-spacing: -1px;
    }
    
    .banner-card {
        position: absolute;
        bottom: -5rem;
        left: 3rem;
        right: 3rem;
        background: white;
        border-radius: 16px;
        padding: 2.5rem;
        box-shadow: 0 15px 35px rgba(0,0,0,0.08);
        display: flex;
        justify-content: space-between;
        align-items: center;
        z-index: 20;
        border: 1px solid #E2E8F0;
    }
    .banner-card-label {
        font-size: 0.75rem;
        font-weight: 700;
        color: #64748B;
        text-transform: uppercase;
        letter-spacing: 1px;
        margin-bottom: 1rem;
    }
    
    .shortcut-card {
        background: white;
        border-radius: 16px;
        padding: 2.5rem 1.5rem;
        text-align: center;
        text-decoration: none;
        color: #1E293B;
        box-shadow: 0 4px 15px rgba(0,0,0,0.02);
        border: 1px solid #E2E8F0;
        transition: all 0.3s;
        display: block;
        height: 100%;
    }
    .shortcut-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 15px 30px rgba(0,0,0,0.06);
        border-color: var(--humana-teal);
    }
    .shortcut-icon {
        width: 64px;
        height: 64px;
        background: #F1F5F9;
        color: var(--humana-teal);
        border-radius: 16px;
        display: flex; align-items: center; justify-content: center;
        font-size: 1.75rem;
        margin: 0 auto 1.5rem;
        transition: all 0.3s;
    }
    .shortcut-card:hover .shortcut-icon {
        background: var(--humana-teal);
        color: white;
        box-shadow: 0 8px 20px rgba(58, 125, 107, 0.3);
    }
    .shortcut-title { font-weight: 700; font-size: 1.25rem; margin-bottom: 0.35rem; }
    .shortcut-desc { font-size: 0.85rem; color: #64748B; }
    
    .section-title { font-size: 1.25rem; font-weight: 700; color: #1E293B; margin-bottom: 1.5rem; text-transform: uppercase; letter-spacing: -0.5px;}

    .stat-card {
        background: white;
        border-radius: 16px;
        padding: 1.5rem;
        border: 1px solid #E2E8F0;
        display: flex; align-items: center; gap: 1.25rem;
        box-shadow: 0 4px 15px rgba(0,0,0,0.02);
    }
    .stat-icon {
        width: 56px; height: 56px; border-radius: 16px;
        display: flex; align-items: center; justify-content: center;
        font-size: 1.5rem;
    }
    .stat-blue { background: #EFF6FF; color: #3B82F6; }
    .stat-green { background: #ECFDF5; color: #10B981; }
    .stat-gray { background: #F8FAFC; color: #64748B; }
</style>

<div class="dashboard-banner">
    <div class="greeting">
        <div class="subtitle">Selamat datang,</div>
        <div class="title">${sessionScope.userName}</div>
    </div>
    
    <div class="banner-card">
        <div class="w-100">
            <div class="banner-card-label">Sesi Terdekat</div>
            <c:choose>
                <c:when test="${not empty jadwalTerdekat}">
                    <h3 class="fw-bold text-dark mb-4">${jadwalTerdekat.namaMateri}</h3>
                    <div class="row">
                        <div class="col-6 border-end">
                            <div class="text-secondary small fw-semibold mb-1">WAKTU</div>
                            <div class="fw-bold text-dark fs-6"><fmt:formatDate value="${jadwalTerdekat.waktuMulai}" pattern="dd MMM yyyy, HH:mm" /></div>
                        </div>
                        <div class="col-6 ps-4">
                            <div class="text-secondary small fw-semibold mb-1">GURU</div>
                            <div class="fw-bold text-dark fs-6">${empty jadwalTerdekat.namaGuru ? '<span class="fst-italic text-muted fw-normal">Menunggu Konfirmasi</span>' : jadwalTerdekat.namaGuru}</div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-3">
                        <i class="bi bi-calendar-x text-muted mb-2 d-block fs-3"></i>
                        <h5 class="fw-bold text-dark mb-1">Belum ada jadwal aktif</h5>
                        <p class="text-secondary small mb-0">Yuk pesan sesi belajarmu sekarang dan mulai tingkatkan prestasimu!</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<div class="row g-4 mb-5">
    <div class="col-md-4">
        <a href="${pageContext.request.contextPath}/pesan" class="shortcut-card">
            <div class="shortcut-icon"><i class="bi bi-book"></i></div>
            <div class="shortcut-title">Pesan Sesi</div>
            <div class="shortcut-desc">Booking jadwal belajarmu</div>
        </a>
    </div>
    <div class="col-md-4">
        <a href="${pageContext.request.contextPath}/materi" class="shortcut-card">
            <div class="shortcut-icon"><i class="bi bi-journal-text"></i></div>
            <div class="shortcut-title">Materi</div>
            <div class="shortcut-desc">Akses materi belajar digital</div>
        </a>
    </div>
    <div class="col-md-4">
        <a href="${pageContext.request.contextPath}/jadwal" class="shortcut-card">
            <div class="shortcut-icon"><i class="bi bi-calendar-event"></i></div>
            <div class="shortcut-title">Jadwal Saya</div>
            <div class="shortcut-desc">Lihat jadwal harianmu</div>
        </a>
    </div>
</div>

<div class="row g-4 mt-2">
    <div class="col-12">
        <h5 class="section-title">Aktivitas & Statistik</h5>
    </div>
    <div class="col-md-4">
        <div class="stat-card">
            <div class="stat-icon stat-blue"><i class="bi bi-calendar-check"></i></div>
            <div>
                <div class="text-secondary small fw-semibold mb-1 text-uppercase letter-spacing-1">Pesanan Aktif</div>
                <div class="fs-4 fw-bold text-dark">${pesananAktif}</div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-card">
            <div class="stat-icon stat-green"><i class="bi bi-play-circle"></i></div>
            <div>
                <div class="text-secondary small fw-semibold mb-1 text-uppercase letter-spacing-1">Sesi Berlangsung</div>
                <div class="fs-4 fw-bold text-dark">${sesiBerlangsung}</div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="stat-card">
            <div class="stat-icon stat-gray"><i class="bi bi-check2-all"></i></div>
            <div>
                <div class="text-secondary small fw-semibold mb-1 text-uppercase letter-spacing-1">Sesi Selesai</div>
                <div class="fs-4 fw-bold text-dark">${sesiSelesai}</div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
