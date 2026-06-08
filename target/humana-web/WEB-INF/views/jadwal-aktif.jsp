<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Jadwal Aktif" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="page-header mb-4">
    <h1 class="fw-bold" style="color: #1E293B;">Jadwal Aktif</h1>
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

<c:choose>
    <c:when test="${empty daftarJadwal}">
        <div class="alert alert-info border-0" style="background-color: #EFF6FF; color: #1D4ED8; border-radius: 0.75rem;">
            <i class="bi bi-info-circle-fill me-2"></i> Belum ada jadwal aktif
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-4">
            <c:forEach items="${daftarJadwal}" var="j">
                <div class="col-md-6">
                    <div class="card shadow-sm border-0 h-100" style="border-radius: 1.25rem;">
                        <div class="card-body p-4 d-flex flex-column">
                            <div class="d-flex justify-content-between align-items-start mb-3">
                                <div>
                                    <h5 class="fw-bold text-dark mb-1">${j.namaMateri}</h5>
                                    <div class="text-secondary" style="font-size: 0.85rem;">${j.namaMapel}</div>
                                </div>
                                <c:choose>
                                    <c:when test="${j.statusPemesanan == 'menunggu konfirmasi'}">
                                        <span class="badge bg-warning text-dark rounded-pill">Menunggu</span>
                                    </c:when>
                                    <c:when test="${j.statusPemesanan == 'dikonfirmasi'}">
                                        <span class="badge bg-primary rounded-pill">Dikonfirmasi</span>
                                    </c:when>
                                    <c:when test="${j.statusPemesanan == 'berlangsung'}">
                                        <span class="badge bg-success rounded-pill">Berlangsung</span>
                                    </c:when>
                                </c:choose>
                            </div>

                            <div class="mb-3 p-3 rounded" style="background-color: #F8FAFC; border: 1px solid #E2E8F0; font-size: 0.9rem;">
                                <c:if test="${sessionScope.userRole == 'MURID'}">
                                    <div class="mb-2"><strong>Guru:</strong> ${empty j.namaGuru ? '<span class="text-muted fst-italic">Menunggu guru...</span>' : j.namaGuru}</div>
                                </c:if>
                                <c:if test="${sessionScope.userRole == 'GURU'}">
                                    <div class="mb-2"><strong>Murid:</strong> ${j.namaMurid}</div>
                                </c:if>
                                <div class="mb-2"><i class="bi bi-clock me-2 text-primary"></i> <fmt:formatDate value="${j.waktuMulai}" pattern="dd MMM yyyy, HH:mm" /> - <fmt:formatDate value="${j.waktuSelesai}" pattern="HH:mm" /></div>
                                <div><i class="bi bi-geo-alt me-2 text-danger"></i> ${j.lokasiSesi}</div>
                            </div>

                            <div class="mt-auto pt-3 border-top d-flex gap-2 justify-content-end">
                                <c:if test="${sessionScope.userRole == 'MURID'}">
                                    <c:choose>
                                        <c:when test="${j.statusPemesanan == 'menunggu konfirmasi'}">
                                            <form action="${pageContext.request.contextPath}/pesan/batal" method="post" style="display:inline;">
                                                <input type="hidden" name="idPemesanan" value="${j.idPemesanan}">
                                                <button type="submit" class="btn btn-outline-danger btn-sm rounded-pill px-3" onclick="return confirm('Batalkan pesanan ini?')">Batalkan</button>
                                            </form>
                                        </c:when>
                                        <c:when test="${j.statusPemesanan == 'dikonfirmasi'}">
                                            <a href="${pageContext.request.contextPath}/bayar?id=${j.idPemesanan}" class="btn btn-primary btn-sm rounded-pill px-4">Bayar</a>
                                        </c:when>
                                        <c:when test="${j.statusPemesanan == 'berlangsung'}">
                                            <span class="badge bg-success rounded-pill px-3 py-2 align-self-center">Sedang Berlangsung</span>
                                        </c:when>
                                    </c:choose>
                                </c:if>

                                <c:if test="${sessionScope.userRole == 'GURU'}">
                                    <c:choose>
                                        <c:when test="${j.statusPemesanan == 'menunggu konfirmasi'}">
                                            <form action="${pageContext.request.contextPath}/jadwal/konfirmasi" method="post" style="display:inline;">
                                                <input type="hidden" name="idPemesanan" value="${j.idPemesanan}">
                                                <input type="hidden" name="aksi" value="terima">
                                                <button type="submit" class="btn btn-success btn-sm rounded-pill px-3">Terima</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/jadwal/konfirmasi" method="post" style="display:inline;">
                                                <input type="hidden" name="idPemesanan" value="${j.idPemesanan}">
                                                <input type="hidden" name="aksi" value="tolak">
                                                <button type="submit" class="btn btn-danger btn-sm rounded-pill px-3" onclick="return confirm('Tolak pesanan ini?')">Tolak</button>
                                            </form>
                                        </c:when>
                                        <c:when test="${j.statusPemesanan == 'dikonfirmasi'}">
                                            <form action="${pageContext.request.contextPath}/jadwal/selesai" method="post" style="display:inline;">
                                                <input type="hidden" name="idPemesanan" value="${j.idPemesanan}">
                                                <button type="submit" class="btn btn-primary btn-sm rounded-pill px-3">Selesaikan Sesi</button>
                                            </form>
                                        </c:when>
                                        <c:when test="${j.statusPemesanan == 'berlangsung'}">
                                            <span class="badge bg-success rounded-pill px-3 py-2 align-self-center">Berlangsung</span>
                                        </c:when>
                                    </c:choose>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
