<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Detail Pembayaran" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="mb-4">
    <a href="${pageContext.request.contextPath}/jadwal" class="text-decoration-none text-secondary d-inline-flex align-items-center" style="font-weight: 500; font-size: 0.9rem; transition: color 0.2s;">
        <i class="bi bi-arrow-left me-2"></i> Kembali ke Jadwal
    </a>
</div>

<div class="page-header mb-4">
    <h1 class="fw-bold" style="color: #1E293B;">Detail Pembayaran</h1>
</div>

<div class="row">
    <!-- Info Sesi -->
    <div class="col-md-7 mb-4">
        <div class="card shadow-sm border-0 h-100" style="border-radius: 1.25rem;">
            <div class="card-header bg-white border-0 p-4 pb-0">
                <h5 class="fw-bold mb-0 text-dark">Informasi Sesi</h5>
            </div>
            <div class="card-body p-4">
                <table class="table table-borderless mb-0">
                    <tbody>
                        <tr>
                            <td class="text-secondary" style="width: 140px;">Nama Guru</td>
                            <td class="fw-semibold text-dark">: ${empty requestScope.namaGuru ? '-' : requestScope.namaGuru}</td>
                        </tr>
                        <tr>
                            <td class="text-secondary">Mata Pelajaran</td>
                            <td class="fw-semibold text-dark">: ${requestScope.namaMapel}</td>
                        </tr>
                        <tr>
                            <td class="text-secondary">Materi</td>
                            <td class="fw-semibold text-dark">: ${requestScope.namaMateri}</td>
                        </tr>
                        <tr>
                            <td class="text-secondary">Waktu</td>
                            <td class="fw-semibold text-dark">: 
                                <fmt:formatDate value="${requestScope.waktuMulai}" pattern="dd MMM yyyy HH:mm" /> - 
                                <fmt:formatDate value="${requestScope.waktuSelesai}" pattern="HH:mm" />
                            </td>
                        </tr>
                        <tr>
                            <td class="text-secondary align-top">Lokasi</td>
                            <td class="fw-semibold text-dark">: ${requestScope.lokasiSesi}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Rincian Biaya -->
    <div class="col-md-5 mb-4">
        <div class="card shadow-sm border-0 h-100" style="border-radius: 1.25rem;">
            <div class="card-header bg-white border-0 p-4 pb-0 text-center">
                <c:choose>
                    <c:when test="${requestScope.statusPembayaran == 'lunas'}">
                        <span class="badge bg-success rounded-pill px-4 py-2 fs-6 mb-2">Sudah Dibayar</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-warning text-dark rounded-pill px-4 py-2 fs-6 mb-2">Menunggu Pembayaran</span>
                    </c:otherwise>
                </c:choose>
                <h5 class="fw-bold text-dark mt-2">Rincian Biaya</h5>
            </div>
            <div class="card-body p-4">
                <div class="d-flex justify-content-between mb-3">
                    <span class="text-secondary">Biaya Sesi</span>
                    <span class="fw-semibold text-dark">
                        <fmt:formatNumber value="${requestScope.biayaSesi}" type="currency" currencySymbol="Rp" maxFractionDigits="0" />
                    </span>
                </div>
                <div class="d-flex justify-content-between mb-3 border-bottom pb-3">
                    <span class="text-secondary">Biaya Transport</span>
                    <span class="fw-semibold text-dark">
                        <fmt:formatNumber value="${requestScope.biayaJarak}" type="currency" currencySymbol="Rp" maxFractionDigits="0" />
                    </span>
                </div>
                <div class="d-flex justify-content-between mb-4">
                    <span class="fw-bold text-dark fs-5">Total</span>
                    <span class="fw-bold fs-4" style="color: #2563EB;">
                        <fmt:formatNumber value="${requestScope.nominal}" type="currency" currencySymbol="Rp" maxFractionDigits="0" />
                    </span>
                </div>

                <form action="${pageContext.request.contextPath}/bayar/proses" method="post">
                    <input type="hidden" name="idPemesanan" value="${requestScope.idPemesanan}">
                    
                    <c:if test="${requestScope.statusPembayaran != 'lunas'}">
                        <div class="mb-4">
                            <label class="form-label fw-semibold text-secondary mb-2" style="font-size: 0.875rem;">Pilih Metode Pembayaran</label>
                            
                            <div class="form-check mb-2 p-3 border rounded d-flex align-items-center" style="background-color: #F8FAFC;">
                                <input class="form-check-input m-0 flex-shrink-0" type="radio" name="metodePembayaran" id="metodeTransfer" value="Transfer Bank" required style="width: 1.25rem; height: 1.25rem;">
                                <label class="form-check-label fw-semibold ms-3 w-100 mb-0" for="metodeTransfer" style="cursor: pointer; padding-top: 2px;">
                                    Transfer Bank
                                </label>
                            </div>
                            
                            <div class="form-check p-3 border rounded d-flex align-items-center" style="background-color: #F8FAFC;">
                                <input class="form-check-input m-0 flex-shrink-0" type="radio" name="metodePembayaran" id="metodeTunai" value="Tunai (COD)" required style="width: 1.25rem; height: 1.25rem;">
                                <label class="form-check-label fw-semibold ms-3 w-100 mb-0" for="metodeTunai" style="cursor: pointer; padding-top: 2px;">
                                    Tunai (Diberikan saat sesi)
                                </label>
                            </div>
                        </div>
                    </c:if>

                    <button type="submit" class="btn btn-primary w-100 py-3 fw-bold shadow-sm" style="border-radius: 0.75rem; background: linear-gradient(135deg, #2563EB 0%, #1D4ED8 100%); border: none;" ${requestScope.statusPembayaran == 'lunas' ? 'disabled' : ''}>
                        <i class="bi ${requestScope.statusPembayaran == 'lunas' ? 'bi-check-circle' : 'bi-credit-card'} me-2"></i>
                        ${requestScope.statusPembayaran == 'lunas' ? 'Sesi Lunas' : 'Bayar Sekarang'}
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
