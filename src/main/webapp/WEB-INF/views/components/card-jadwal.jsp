<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="st" value="${fn:toLowerCase(j.statusPemesanan)}" />
<c:set var="isLunas" value="${fn:toLowerCase(j.statusPembayaran) == 'lunas'}" />

<c:set var="personLabel" value="${sessionScope.userRole == 'MURID' ? 'Guru' : 'Murid'}" />
<c:set var="personName">
    <c:choose>
        <c:when test="${sessionScope.userRole == 'MURID'}">
            <c:choose>
                <c:when test="${not empty j.namaGuru}">${j.namaGuru}</c:when>
                <c:otherwise><span class="text-muted fst-italic">Menunggu guru...</span></c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>${j.namaMurid}</c:otherwise>
    </c:choose>
</c:set>

<c:set var="badgeHtml">
    <c:choose>
        <c:when test="${st == 'menunggu konfirmasi'}">
            <span class="badge text-dark rounded-pill" style="background-color: #FBBF24;">Menunggu</span>
        </c:when>
        <c:when test="${st == 'dikonfirmasi'}">
            <span class="badge text-white rounded-pill" style="background-color: #2B4C7E;">Dikonfirmasi</span>
        </c:when>
        <c:when test="${st == 'berlangsung'}">
            <span class="badge bg-success rounded-pill">Berlangsung</span>
        </c:when>
        <c:otherwise>
            <span class="badge bg-secondary rounded-pill">${j.statusPemesanan}</span>
        </c:otherwise>
    </c:choose>
</c:set>

<div class="col-md-6">
    <div class="card shadow-sm border-0 h-100" style="border-radius:1.25rem;">
        <div class="card-body p-4 d-flex flex-column">
            <div class="d-flex justify-content-between align-items-start mb-3">
                <div>
                    <h5 class="fw-bold text-dark mb-1">${j.namaMateri}</h5>
                    <div class="text-secondary" style="font-size:0.85rem;">${j.namaMapel}</div>
                </div>
                ${badgeHtml}
            </div>
            
            <div class="mb-3 p-3 rounded" style="background:#F8FAFC;border:1px solid #E2E8F0;font-size:0.9rem;">
                <div class="mb-2"><strong>${personLabel}:</strong> ${personName}</div>
                <div class="mb-2">
                    <i class="bi bi-clock me-2 text-primary"></i>
                    <fmt:formatDate value="${j.waktuMulai}" pattern="dd MMM yyyy, HH:mm" /> &ndash; <fmt:formatDate value="${j.waktuSelesai}" pattern="HH:mm" />
                </div>
                <div><i class="bi bi-geo-alt me-2 text-danger"></i>${j.lokasiSesi}</div>
            </div>
            
            <div class="mt-auto pt-3 border-top d-flex gap-2 justify-content-end">
                <c:choose>
                    <c:when test="${sessionScope.userRole == 'MURID'}">
                        <c:if test="${st == 'menunggu konfirmasi'}">
                            <a href="${pageContext.request.contextPath}/pesan/menunggu?id=${j.idPemesanan}" class="btn btn-outline-primary btn-sm rounded-pill px-3 me-1">Lihat Status</a>
                            <button type="button" class="btn btn-outline-danger btn-sm rounded-pill px-3" 
                                    onclick="showKonfirmasi('Batalkan pesanan?', 'Apakah kamu yakin ingin membatalkan pesanan ini?', function(){ submitForm('${pageContext.request.contextPath}/pesan/batal', {idPemesanan:${j.idPemesanan}}) })">
                                Batalkan
                            </button>
                        </c:if>
                        <c:if test="${st == 'dikonfirmasi'}">
                            <c:choose>
                                <c:when test="${!isLunas}">
                                    <a href="${pageContext.request.contextPath}/bayar?id=${j.idPemesanan}" class="btn btn-primary btn-sm rounded-pill px-4">Bayar</a>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge text-dark rounded-pill px-3 py-2" style="background-color: #FBBF24;">Menunggu Waktu Sesi</span>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <c:if test="${st == 'berlangsung'}">
                            <span class="badge bg-success rounded-pill px-3 py-2">Sedang Berlangsung</span>
                        </c:if>
                    </c:when>
                    <c:when test="${sessionScope.userRole == 'GURU'}">
                        <c:if test="${st == 'menunggu konfirmasi'}">
                            <button type="button" class="btn btn-success btn-sm rounded-pill px-3" 
                                    onclick="showKonfirmasi('Terima permintaan?', 'Kamu akan menerima sesi belajar dari murid ini. Lanjutkan?', function(){ submitForm('${pageContext.request.contextPath}/jadwal/konfirmasi', {idPemesanan:${j.idPemesanan}, aksi:'terima'}) })">
                                Terima
                            </button>
                        </c:if>
                        <c:if test="${st == 'dikonfirmasi'}">
                            <button type="button" class="btn btn-outline-danger btn-sm rounded-pill px-3 me-1" 
                                    onclick="showKonfirmasi('Batalkan sesi?', 'Sesi akan dikembalikan ke pencarian guru. Murid akan mencari guru lain.', function(){ submitForm('${pageContext.request.contextPath}/jadwal/batal-guru', {idPemesanan:${j.idPemesanan}}) }, true)">
                                Batalkan Sesi
                            </button>
                            <c:choose>
                                <c:when test="${isLunas}">
                                    <button type="button" class="btn btn-primary btn-sm rounded-pill px-3" 
                                            onclick="showKonfirmasi('Selesaikan sesi?', 'Tandai sesi ini sebagai selesai?', function(){ submitForm('${pageContext.request.contextPath}/jadwal/selesai', {idPemesanan:${j.idPemesanan}}) })">
                                        Selesaikan Sesi
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button type="button" class="btn btn-secondary btn-sm rounded-pill px-3" disabled title="Menunggu pembayaran murid">Selesaikan Sesi</button>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <c:if test="${st == 'berlangsung'}">
                            <c:choose>
                                <c:when test="${isLunas}">
                                    <button type="button" class="btn btn-primary btn-sm rounded-pill px-3" 
                                            onclick="showKonfirmasi('Selesaikan sesi?', 'Tandai sesi ini sebagai selesai?', function(){ submitForm('${pageContext.request.contextPath}/jadwal/selesai', {idPemesanan:${j.idPemesanan}}) })">
                                        Selesaikan Sesi
                                    </button>
                                </c:when>
                                <c:otherwise>
                                    <button type="button" class="btn btn-secondary btn-sm rounded-pill px-3" disabled title="Menunggu pembayaran murid">Selesaikan Sesi</button>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </c:when>
                </c:choose>
            </div>
        </div>
    </div>
</div>
