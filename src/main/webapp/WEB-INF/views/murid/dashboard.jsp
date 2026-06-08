<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Dashboard Murid" />
<c:set var="activePage" value="dashboard" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="page-header mb-4 d-flex justify-content-between align-items-center">
    <div>
        <h1 class="fw-bold mb-1" style="color: #1E293B;">Halo, ${sessionScope.userName}! 👋</h1>
        <p class="text-secondary mb-0">Selamat datang kembali di dashboard pembelajaranmu.</p>
    </div>
</div>

<!-- Shortcuts -->
<div class="row g-3 mb-4">
    <div class="col-6 col-md-3">
        <a href="${pageContext.request.contextPath}/pesan" class="btn btn-primary w-100 py-3 fw-semibold shadow-sm" style="border-radius: 1rem;">
            <i class="bi bi-calendar-plus d-block fs-4 mb-1"></i> Pesan Sesi
        </a>
    </div>
    <div class="col-6 col-md-3">
        <a href="${pageContext.request.contextPath}/jadwal" class="btn btn-outline-primary bg-white w-100 py-3 fw-semibold shadow-sm" style="border-radius: 1rem;">
            <i class="bi bi-calendar-event d-block fs-4 mb-1"></i> Jadwal Aktif
        </a>
    </div>
    <div class="col-6 col-md-3">
        <a href="${pageContext.request.contextPath}/histori" class="btn btn-outline-primary bg-white w-100 py-3 fw-semibold shadow-sm" style="border-radius: 1rem;">
            <i class="bi bi-clock-history d-block fs-4 mb-1"></i> Riwayat
        </a>
    </div>
    <div class="col-6 col-md-3">
        <a href="${pageContext.request.contextPath}/profil" class="btn btn-outline-primary bg-white w-100 py-3 fw-semibold shadow-sm" style="border-radius: 1rem;">
            <i class="bi bi-person d-block fs-4 mb-1"></i> Profil
        </a>
    </div>
</div>

<!-- Stats -->
<div class="row g-4 mb-5">
    <div class="col-md-4">
        <div class="card shadow-sm border-0 h-100" style="border-radius: 1.25rem; background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%); border-left: 5px solid #2563EB !important;">
            <div class="card-body p-4 d-flex justify-content-between align-items-center">
                <div>
                    <div class="text-secondary fw-semibold mb-2">Pesanan Aktif</div>
                    <div class="fs-1 fw-bold text-primary">${pesananAktif}</div>
                </div>
                <i class="bi bi-calendar-check text-primary opacity-50" style="font-size: 3rem;"></i>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card shadow-sm border-0 h-100" style="border-radius: 1.25rem; background: linear-gradient(135deg, #ECFDF5 0%, #D1FAE5 100%); border-left: 5px solid #10B981 !important;">
            <div class="card-body p-4 d-flex justify-content-between align-items-center">
                <div>
                    <div class="text-secondary fw-semibold mb-2">Sesi Berlangsung</div>
                    <div class="fs-1 fw-bold text-success">${sesiBerlangsung}</div>
                </div>
                <i class="bi bi-play-circle text-success opacity-50" style="font-size: 3rem;"></i>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card shadow-sm border-0 h-100" style="border-radius: 1.25rem; background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%); border-left: 5px solid #64748B !important;">
            <div class="card-body p-4 d-flex justify-content-between align-items-center">
                <div>
                    <div class="text-secondary fw-semibold mb-2">Sesi Selesai</div>
                    <div class="fs-1 fw-bold text-dark">${sesiSelesai}</div>
                </div>
                <i class="bi bi-check2-all text-secondary opacity-50" style="font-size: 3rem;"></i>
            </div>
        </div>
    </div>
</div>

<!-- Jadwal Terdekat -->
<h4 class="fw-bold mb-3" style="color: #1E293B;">Jadwal Terdekat</h4>
<c:choose>
    <c:when test="${not empty jadwalTerdekat}">
        <div class="card shadow-sm border-0" style="border-radius: 1.25rem; border-left: 5px solid #3B82F6 !important;">
            <div class="card-body p-4 d-flex flex-column flex-md-row justify-content-between align-items-md-center">
                <div class="mb-3 mb-md-0">
                    <div class="d-flex align-items-center mb-2">
                        <h5 class="fw-bold text-dark mb-0 me-3">${jadwalTerdekat.namaMateri}</h5>
                        <c:choose>
                            <c:when test="${jadwalTerdekat.statusPemesanan == 'menunggu konfirmasi'}">
                                <span class="badge bg-warning text-dark rounded-pill">Menunggu</span>
                            </c:when>
                            <c:when test="${jadwalTerdekat.statusPemesanan == 'dikonfirmasi'}">
                                <span class="badge bg-primary rounded-pill">Dikonfirmasi</span>
                            </c:when>
                            <c:when test="${jadwalTerdekat.statusPemesanan == 'berlangsung'}">
                                <span class="badge bg-success rounded-pill">Berlangsung</span>
                            </c:when>
                        </c:choose>
                    </div>
                    <div class="text-secondary mb-1">
                        <i class="bi bi-person me-2"></i> Guru: ${empty jadwalTerdekat.namaGuru ? '<span class="fst-italic text-muted">Menunggu Konfirmasi...</span>' : jadwalTerdekat.namaGuru}
                    </div>
                    <div class="text-secondary">
                        <i class="bi bi-clock me-2 text-primary"></i> <fmt:formatDate value="${jadwalTerdekat.waktuMulai}" pattern="dd MMM yyyy, HH:mm" />
                    </div>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/jadwal" class="btn btn-outline-primary rounded-pill px-4 fw-semibold">Lihat Detail</a>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="card shadow-sm border-0" style="border-radius: 1.25rem;">
            <div class="card-body p-5 text-center">
                <div class="mb-3">
                    <i class="bi bi-calendar-x text-muted" style="font-size: 3rem;"></i>
                </div>
                <h5 class="fw-bold text-dark mb-2">Belum ada jadwal aktif</h5>
                <p class="text-secondary mb-4">Yuk pesan sesi belajarmu sekarang dan mulai tingkatkan prestasimu!</p>
                <a href="${pageContext.request.contextPath}/pesan" class="btn btn-primary rounded-pill px-5 py-2 fw-bold shadow-sm">Pesan Sekarang</a>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
