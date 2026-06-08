<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Dashboard Guru" />
<c:set var="activePage" value="dashboard" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="page-header mb-4 d-flex justify-content-between align-items-center flex-wrap">
    <div>
        <h1 class="fw-bold mb-1" style="color: #1E293B;">Halo, ${sessionScope.userName}! 👋</h1>
        <p class="text-secondary mb-0">Selamat datang di dashboard pengajar.</p>
    </div>
    <div class="mt-3 mt-md-0 bg-white px-4 py-2 rounded-pill shadow-sm border d-flex align-items-center">
        <i class="bi bi-star-fill text-warning fs-4 me-2"></i>
        <span class="fw-bold fs-5 text-dark"><fmt:formatNumber value="${rating}" maxFractionDigits="1" minFractionDigits="1"/> <span class="text-secondary fs-6 fw-normal">/ 5.0</span></span>
    </div>
</div>

<!-- Shortcuts -->
<div class="row g-3 mb-4">
    <div class="col-6 col-md-3">
        <a href="${pageContext.request.contextPath}/jadwal" class="btn btn-outline-primary bg-white w-100 py-3 fw-semibold shadow-sm position-relative" style="border-radius: 1rem;">
            <i class="bi bi-bell d-block fs-4 mb-1"></i> Permintaan
            <c:if test="${permintaanMasuk > 0}">
                <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger border border-light">
                    ${permintaanMasuk}
                </span>
            </c:if>
        </a>
    </div>
    <div class="col-6 col-md-3">
        <a href="${pageContext.request.contextPath}/jadwal" class="btn btn-primary w-100 py-3 fw-semibold shadow-sm" style="border-radius: 1rem;">
            <i class="bi bi-calendar-event d-block fs-4 mb-1"></i> Jadwal
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
        <div class="card shadow-sm border-0 h-100" style="border-radius: 1.25rem; background: linear-gradient(135deg, #FEF2F2 0%, #FEE2E2 100%); border-left: 5px solid #EF4444 !important;">
            <div class="card-body p-4 d-flex justify-content-between align-items-center">
                <div>
                    <div class="text-secondary fw-semibold mb-2">Permintaan Masuk</div>
                    <div class="fs-1 fw-bold text-danger">${permintaanMasuk}</div>
                </div>
                <i class="bi bi-envelope-open text-danger opacity-50" style="font-size: 3rem;"></i>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card shadow-sm border-0 h-100" style="border-radius: 1.25rem; background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%); border-left: 5px solid #3B82F6 !important;">
            <div class="card-body p-4 d-flex justify-content-between align-items-center">
                <div>
                    <div class="text-secondary fw-semibold mb-2">Sesi Aktif</div>
                    <div class="fs-1 fw-bold text-primary">${sesiAktif}</div>
                </div>
                <i class="bi bi-calendar-check text-primary opacity-50" style="font-size: 3rem;"></i>
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

<!-- Permintaan Terbaru -->
<h4 class="fw-bold mb-3" style="color: #1E293B;">Permintaan Terbaru</h4>
<div class="row g-4">
    <c:choose>
        <c:when test="${empty permintaanTerbaru}">
            <div class="col-12">
                <div class="alert alert-light border-0 py-4 text-center text-secondary" style="border-radius: 1rem; background-color: #F8FAFC;">
                    <i class="bi bi-inbox fs-3 d-block mb-2 text-muted"></i>
                    Belum ada permintaan masuk terbaru.
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <c:forEach items="${permintaanTerbaru}" var="p">
                <div class="col-md-4">
                    <div class="card shadow-sm border-0 h-100" style="border-radius: 1.25rem; border-top: 5px solid #F59E0B;">
                        <div class="card-body p-4 text-center">
                            <div class="bg-warning bg-opacity-10 rounded-circle d-inline-flex justify-content-center align-items-center mb-3 text-warning" style="width: 60px; height: 60px; font-size: 1.5rem;">
                                <i class="bi bi-person-fill"></i>
                            </div>
                            <h5 class="fw-bold text-dark mb-1">${p.namaMurid}</h5>
                            <div class="text-secondary fw-semibold mb-3">${p.namaMateri}</div>
                            
                            <div class="d-inline-block bg-light rounded-pill px-3 py-2 mb-4 text-secondary" style="font-size: 0.85rem;">
                                <i class="bi bi-clock me-1"></i> <fmt:formatDate value="${p.waktuMulai}" pattern="dd MMM yyyy, HH:mm" />
                            </div>
                            
                            <div>
                                <a href="${pageContext.request.contextPath}/jadwal" class="btn btn-outline-primary w-100 rounded-pill fw-semibold">Lihat Detail</a>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
