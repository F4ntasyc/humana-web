<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="Riwayat Sesi" />
<c:set var="activePage" value="riwayat" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="page-header mb-4">
    <h1 class="fw-bold" style="color: #1E293B;">Riwayat Sesi</h1>
</div>

<c:if test="${param.feedback == '1'}">
    <div class="alert-custom alert-success-custom"><i class="bi bi-check-circle-fill"></i> Ulasan berhasil dikirim. Terima kasih!</div>
</c:if>
<c:if test="${not empty error or not empty param.error}">
    <div class="alert-custom alert-danger-custom"><i class="bi bi-exclamation-circle-fill"></i> ${not empty error ? error : param.error}</div>
</c:if>

<div class="card shadow-sm border-0" style="border-radius: 1.25rem;">
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover mb-0 align-middle">
                <thead style="background-color: #F8FAFC;">
                    <tr>
                        <th class="ps-4 py-3 text-secondary" style="font-weight: 600;">No</th>
                        <th class="py-3 text-secondary" style="font-weight: 600;">Materi</th>
                        <th class="py-3 text-secondary" style="font-weight: 600;">${sessionScope.userRole == 'MURID' ? 'Guru' : 'Murid'}</th>
                        <th class="py-3 text-secondary" style="font-weight: 600;">Tanggal</th>
                        <th class="py-3 text-secondary" style="font-weight: 600;">Durasi</th>
                        <th class="py-3 text-secondary" style="font-weight: 600;">Biaya</th>
                        <th class="py-3 text-secondary" style="font-weight: 600;">Status</th>
                        <c:if test="${sessionScope.userRole == 'MURID'}">
                            <th class="pe-4 py-3 text-secondary" style="font-weight: 600;">Aksi</th>
                        </c:if>
                        <c:if test="${sessionScope.userRole == 'GURU'}">
                            <th class="pe-4 py-3 text-secondary" style="font-weight: 600;">Rating</th>
                        </c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty daftarHistori}">
                            <tr>
                                <td colspan="${sessionScope.userRole == 'MURID' ? 8 : 7}" class="text-center py-5 text-muted">
                                    Belum ada riwayat sesi.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${daftarHistori}" var="h" varStatus="loop">
                                <tr>
                                    <td class="ps-4 text-secondary">${loop.index + 1}</td>
                                    <td>
                                        <div class="fw-semibold text-dark">${h.namaMateri}</div>
                                        <div class="text-muted" style="font-size: 0.8rem;">${h.namaMapel}</div>
                                    </td>
                                    <td>${sessionScope.userRole == 'MURID' ? h.namaGuru : h.namaMurid}</td>
                                    <td>
                                        <fmt:formatDate value="${h.waktuMulai}" pattern="dd MMM yyyy" /><br>
                                        <small class="text-muted"><fmt:formatDate value="${h.waktuMulai}" pattern="HH:mm" /> - <fmt:formatDate value="${h.waktuSelesai}" pattern="HH:mm" /></small>
                                    </td>
                                    <td>${h.durasiMenit} mnt</td>
                                    <td><fmt:formatNumber value="${h.nominal}" type="currency" currencySymbol="Rp" maxFractionDigits="0" /></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${h.statusPemesanan == 'selesai'}">
                                                <span class="badge bg-success bg-opacity-10 text-success rounded-pill px-3">Selesai</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger bg-opacity-10 text-danger rounded-pill px-3">Dibatalkan</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <c:if test="${sessionScope.userRole == 'MURID'}">
                                        <td class="pe-4">
                                            <c:choose>
                                                <c:when test="${h.statusPemesanan == 'selesai' and empty h.rating}">
                                                    <button type="button" class="btn btn-outline-warning btn-sm rounded-pill" onclick="openFeedbackModal(${h.idPemesanan}, '${h.namaGuru}', '${h.namaMateri}')">
                                                        <i class="bi bi-star"></i> Beri Ulasan
                                                    </button>
                                                </c:when>
                                                <c:when test="${not empty h.rating}">
                                                    <div class="text-warning">
                                                        <c:forEach begin="1" end="5" var="i">
                                                            <i class="bi ${i <= h.rating ? 'bi-star-fill' : 'bi-star'}"></i>
                                                        </c:forEach>
                                                    </div>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                    </c:if>
                                    <c:if test="${sessionScope.userRole == 'GURU'}">
                                        <td class="pe-4">
                                            <c:choose>
                                                <c:when test="${not empty h.rating}">
                                                    <div class="text-warning">
                                                        <c:forEach begin="1" end="5" var="i">
                                                            <i class="bi ${i <= h.rating ? 'bi-star-fill' : 'bi-star'}"></i>
                                                        </c:forEach>
                                                        <span class="text-dark ms-1" style="font-size:0.85rem;">(${h.rating}/5)</span>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted" style="font-size:0.85rem;">Belum ada</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Modal Feedback -->
<div class="modal fade" id="feedbackModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow" style="border-radius: 1rem;">
            <div class="modal-header border-0 pb-0">
                <h5 class="modal-title fw-bold">Beri Ulasan Sesi</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form action="${pageContext.request.contextPath}/histori/feedback" method="post">
                <div class="modal-body">
                    <input type="hidden" name="idPemesanan" id="fbIdPemesanan">
                    
                    <div class="text-center mb-4">
                        <div class="text-muted mb-1" id="fbMateri" style="font-size: 0.9rem;"></div>
                        <div class="fw-semibold fs-5" id="fbGuru"></div>
                    </div>

                    <div class="mb-3 text-center">
                        <label class="form-label fw-semibold">Rating</label>
                        <div class="rating-input d-flex justify-content-center flex-row-reverse">
                            <input type="radio" id="star5" name="rating" value="5" required/><label for="star5" title="5 stars"><i class="bi bi-star-fill"></i></label>
                            <input type="radio" id="star4" name="rating" value="4" /><label for="star4" title="4 stars"><i class="bi bi-star-fill"></i></label>
                            <input type="radio" id="star3" name="rating" value="3" /><label for="star3" title="3 stars"><i class="bi bi-star-fill"></i></label>
                            <input type="radio" id="star2" name="rating" value="2" /><label for="star2" title="2 stars"><i class="bi bi-star-fill"></i></label>
                            <input type="radio" id="star1" name="rating" value="1" /><label for="star1" title="1 star"><i class="bi bi-star-fill"></i></label>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="komentar" class="form-label fw-semibold">Komentar</label>
                        <textarea class="form-control" id="komentar" name="komentar" rows="3" placeholder="Bagaimana pengalaman belajarmu?" style="border-radius: 0.75rem;"></textarea>
                    </div>
                </div>
                <div class="modal-footer border-0 pt-0">
                    <button type="button" class="btn btn-light rounded-pill px-4" data-bs-dismiss="modal">Batal</button>
                    <button type="submit" class="btn btn-warning rounded-pill px-4 text-dark fw-bold">Kirim Ulasan</button>
                </div>
            </form>
        </div>
    </div>
</div>

<style>
.rating-input {
    gap: 0.5rem;
}
.rating-input input {
    display: none;
}
.rating-input label {
    cursor: pointer;
    font-size: 2rem;
    color: #E2E8F0;
    transition: color 0.2s;
}
.rating-input label:hover,
.rating-input label:hover ~ label,
.rating-input input:checked ~ label {
    color: #FBBF24;
}
</style>

<script>
    function openFeedbackModal(idPemesanan, guru, materi) {
        document.getElementById('fbIdPemesanan').value = idPemesanan;
        document.getElementById('fbGuru').textContent = 'Guru: ' + guru;
        document.getElementById('fbMateri').textContent = materi;
        
        var modal = new bootstrap.Modal(document.getElementById('feedbackModal'));
        modal.show();
    }
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
