<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Mencari Pengajar" />
<c:set var="activePage" value="pesan" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<style>
    .menunggu-wrap {
        max-width: 560px;
        margin: 0 auto;
        padding: 2rem 0 3rem;
    }
    .searching-card {
        background: #fff;
        border-radius: 15px;
        padding: 18px;
        display: flex;
        align-items: center;
        gap: 15px;
        box-shadow: 0 3px 16px rgba(0, 0, 0, 0.1);
        margin-bottom: 1.75rem;
        text-align: left;
    }
    .search-spin-icon {
        width: 42px;
        height: 42px;
        flex-shrink: 0;
        object-fit: contain;
        animation: spin-magnifier 2.5s linear infinite;
    }
    @keyframes spin-magnifier {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }
    .searching-title {
        font-size: 0.95rem;
        font-weight: 700;
        color: #1A1A1A;
        margin: 0 0 0.2rem;
        line-height: 1.35;
    }
    .searching-subtitle {
        font-size: 0.8rem;
        color: #888;
        margin: 0;
    }
</style>

<div class="menunggu-wrap">
    <div class="searching-card">
        <img src="${pageContext.request.contextPath}/assets/images/mencari_icon.png"
             alt="" class="search-spin-icon" aria-hidden="true">
        <div>
            <p class="searching-title">Mencari pengajar terbaik untukmu...</p>
            <p class="searching-subtitle">Perkiraan kurang dari 30 detik.</p>
        </div>
    </div>

    <div class="card shadow-sm border-0" style="border-radius: 1.25rem;">
        <div class="card-body p-4 text-start">
            <h5 class="fw-bold mb-1">${namaMateri}</h5>
            <p class="text-secondary small mb-3">${namaMapel}</p>
            <div class="mb-2">
                <i class="bi bi-clock me-2 text-primary"></i>
                <fmt:formatDate value="${waktuMulai}" pattern="dd MMM yyyy, HH:mm" />
                &ndash;
                <fmt:formatDate value="${waktuSelesai}" pattern="HH:mm" />
            </div>
            <span class="badge bg-warning text-dark rounded-pill">Menunggu Konfirmasi Guru</span>
        </div>
    </div>

    <div class="mt-4 text-center">
        <form action="${pageContext.request.contextPath}/pesan/batal" method="post" class="d-inline">
            <input type="hidden" name="idPemesanan" value="${idPemesanan}">
            <button type="submit" class="btn btn-outline-danger rounded-pill px-4"
                    onclick="return confirm('Batalkan permintaan ini?')">
                Batalkan Permintaan
            </button>
        </form>
        <a href="${pageContext.request.contextPath}/jadwal" class="btn btn-link text-secondary ms-2">Lihat Jadwal</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
