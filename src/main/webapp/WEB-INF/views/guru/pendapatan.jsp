<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Pendapatan" />
<c:set var="activePage" value="pendapatan" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="page-header mb-4">
    <h1 class="fw-bold" style="color: #1E293B;">Pendapatan</h1>
    <p class="text-secondary mb-0">Ringkasan pendapatan dari sesi yang telah selesai dan lunas.</p>
</div>

<c:if test="${not empty error}">
    <div class="alert-custom alert-danger-custom"><i class="bi bi-exclamation-circle-fill"></i> ${error}</div>
</c:if>

<div class="row g-4 mb-4">
    <div class="col-md-4">
        <div class="card border-0 shadow-sm h-100" style="border-radius: 1.25rem; background: linear-gradient(135deg, #1E365C, #2563EB); color: white;">
            <div class="card-body p-4">
                <div class="small text-uppercase fw-bold opacity-75 mb-2">Total Pendapatan</div>
                <div class="fs-2 fw-bold">
                    <fmt:formatNumber value="${totalPendapatan}" type="currency" currencySymbol="Rp" maxFractionDigits="0"/>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card border-0 shadow-sm h-100" style="border-radius: 1.25rem;">
            <div class="card-body p-4">
                <div class="small text-secondary text-uppercase fw-bold mb-2">Bulan Ini</div>
                <div class="fs-3 fw-bold text-dark">
                    <fmt:formatNumber value="${bulanIni}" type="currency" currencySymbol="Rp" maxFractionDigits="0"/>
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-4">
        <div class="card border-0 shadow-sm h-100" style="border-radius: 1.25rem;">
            <div class="card-body p-4">
                <div class="small text-secondary text-uppercase fw-bold mb-2">Sesi Selesai</div>
                <div class="fs-3 fw-bold text-dark">${sesiSelesai} <span class="fs-6 fw-normal text-secondary">sesi</span></div>
            </div>
        </div>
    </div>
</div>

<div class="card border-0 shadow-sm" style="border-radius: 1.25rem;">
    <div class="card-header bg-white border-0 p-4 pb-0">
        <h5 class="fw-bold mb-0">Riwayat Pendapatan Terbaru</h5>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover mb-0 align-middle">
                <thead style="background: #F8FAFC;">
                    <tr>
                        <th class="ps-4 py-3">Materi</th>
                        <th class="py-3">Murid</th>
                        <th class="py-3">Tanggal</th>
                        <th class="py-3">Nominal</th>
                        <th class="pe-4 py-3">Status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty riwayatPendapatan}">
                            <tr><td colspan="5" class="text-center py-5 text-muted">Belum ada riwayat pendapatan.</td></tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${riwayatPendapatan}" var="r">
                                <tr>
                                    <td class="ps-4">
                                        <div class="fw-semibold">${r.namaMateri}</div>
                                        <small class="text-muted">${r.namaMapel}</small>
                                    </td>
                                    <td>${r.namaMurid}</td>
                                    <td><fmt:formatDate value="${r.waktuMulai}" pattern="dd MMM yyyy HH:mm"/></td>
                                    <td class="fw-semibold">
                                        <fmt:formatNumber value="${r.nominal}" type="currency" currencySymbol="Rp" maxFractionDigits="0"/>
                                    </td>
                                    <td class="pe-4">
                                        <span class="badge bg-success bg-opacity-10 text-success rounded-pill">Lunas</span>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
