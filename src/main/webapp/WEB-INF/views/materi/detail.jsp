<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Detail Materi - ${materi.namaMateri}" />
<c:set var="activePage" value="materi" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="mb-4">
    <a href="${pageContext.request.contextPath}/materi" class="text-decoration-none text-secondary d-inline-flex align-items-center" style="font-weight: 500; font-size: 0.9rem; transition: color 0.2s;">
        <i class="bi bi-arrow-left me-2"></i> Kembali ke Daftar Materi
    </a>
</div>

<div class="card shadow-sm border-0" style="border-radius: 1.25rem; overflow: hidden;">
    <div class="card-header bg-white p-4 border-0 pb-0">
        <div class="d-flex justify-content-between align-items-center">
            <h2 class="fw-bold mb-0 text-dark" style="color: #1E293B;">${materi.namaMateri}</h2>
            <c:choose>
                <c:when test="${materi.jenjang == 'SD'}"><span class="badge bg-info text-white rounded-pill px-3 py-2 fw-semibold" style="font-size: 0.8rem; letter-spacing: 0.5px;">SD</span></c:when>
                <c:when test="${materi.jenjang == 'SMP'}"><span class="badge bg-warning text-dark rounded-pill px-3 py-2 fw-semibold" style="font-size: 0.8rem; letter-spacing: 0.5px;">SMP</span></c:when>
                <c:when test="${materi.jenjang == 'SMA'}"><span class="badge bg-success rounded-pill px-3 py-2 fw-semibold" style="font-size: 0.8rem; letter-spacing: 0.5px;">SMA</span></c:when>
                <c:when test="${materi.jenjang == 'SMK'}"><span class="badge bg-primary rounded-pill px-3 py-2 fw-semibold" style="font-size: 0.8rem; letter-spacing: 0.5px;">SMK</span></c:when>
                <c:otherwise><span class="badge bg-secondary rounded-pill px-3 py-2 fw-semibold" style="font-size: 0.8rem; letter-spacing: 0.5px;">${materi.jenjang}</span></c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <div class="card-body p-4">
        <div class="row mb-4 bg-light rounded-3 p-3 mx-0">
            <div class="col-md-6 mb-3 mb-md-0 d-flex align-items-center">
                <div class="bg-white rounded-circle d-flex align-items-center justify-content-center shadow-sm me-3" style="width: 48px; height: 48px;">
                    <i class="bi bi-journal-bookmark text-primary fs-5"></i>
                </div>
                <div>
                    <div class="text-muted" style="font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Mata Pelajaran</div>
                    <div class="fw-semibold text-dark">${materi.namaMapel}</div>
                </div>
            </div>
            <div class="col-md-6 d-flex align-items-center">
                <div class="bg-white rounded-circle d-flex align-items-center justify-content-center shadow-sm me-3" style="width: 48px; height: 48px;">
                    <i class="bi bi-mortarboard text-primary fs-5"></i>
                </div>
                <div>
                    <div class="text-muted" style="font-size: 0.8rem; font-weight: 600; text-transform: uppercase;">Kelas & Jurusan</div>
                    <div class="fw-semibold text-dark">Kelas ${materi.kelas} ${not empty materi.jurusan and materi.jurusan != '(NULL)' ? '- '.concat(materi.jurusan) : ''}</div>
                </div>
            </div>
        </div>

        <div class="mb-5">
            <h5 class="fw-bold mb-3" style="color: #1E293B; font-size: 1.1rem;">Deskripsi Materi</h5>
            <div class="p-4 bg-white border rounded-3 text-secondary" style="font-size: 0.95rem; line-height: 1.7; white-space: pre-wrap;">${materi.deskripsi}</div>
        </div>

        <c:if test="${sessionScope.userRole == 'MURID'}">
            <div class="d-flex justify-content-end border-top pt-4">
                <a href="${pageContext.request.contextPath}/pesan?idMateri=${materi.idMateri}" class="btn btn-primary" style="border-radius: 0.75rem; padding: 0.75rem 2rem; font-weight: 600; background-color: var(--humana-teal); border: none; box-shadow: 0 4px 14px 0 rgba(58, 125, 107, 0.35);">
                    <i class="bi bi-calendar-plus me-2"></i> Pesan Sesi dengan Materi Ini
                </a>
            </div>
        </c:if>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
