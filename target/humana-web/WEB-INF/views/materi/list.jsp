<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="Daftar Materi" />
<c:set var="pageDescription" value="Pilih materi les privat yang Anda inginkan" />
<c:set var="activePage" value="materi" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="page-header">
    <h1>Daftar Materi</h1>
    <p>Temukan materi yang sesuai dengan kebutuhan Anda.</p>
</div>

<!-- Filter Form -->
<div class="card shadow-sm border-0 mb-4" style="border-radius: 1rem;">
    <div class="card-body p-4">
        <form action="${pageContext.request.contextPath}/materi" method="get" class="row g-3 align-items-end">
            <div class="col-md-4">
                <label for="jenjang" class="form-label fw-semibold text-secondary mb-1" style="font-size: 0.875rem;">Filter Jenjang</label>
                <select name="jenjang" id="jenjang" class="form-select" style="border-radius: 0.625rem; border-color: #E2E8F0; padding: 0.6rem 1rem;">
                    <option value="Semua" ${jenjangFilter == 'Semua' or empty jenjangFilter ? 'selected' : ''}>Semua Jenjang</option>
                    <option value="SD" ${jenjangFilter == 'SD' ? 'selected' : ''}>SD</option>
                    <option value="SMP" ${jenjangFilter == 'SMP' ? 'selected' : ''}>SMP</option>
                    <option value="SMA" ${jenjangFilter == 'SMA' ? 'selected' : ''}>SMA</option>
                    <option value="SMK" ${jenjangFilter == 'SMK' ? 'selected' : ''}>SMK</option>
                </select>
            </div>
            <div class="col-md-auto">
                <button type="submit" class="btn btn-primary px-4" style="border-radius: 0.625rem; padding: 0.6rem 1.5rem; background-color: #2563EB; border: none; font-weight: 500;">
                    <i class="bi bi-funnel me-1"></i> Filter
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Grid Materi -->
<c:choose>
    <c:when test="${empty daftarMateri}">
        <div class="alert alert-info border-0" style="background-color: #EFF6FF; color: #1D4ED8; border-radius: 0.75rem;">
            <i class="bi bi-info-circle-fill me-2"></i> Belum ada materi tersedia
        </div>
    </c:when>
    <c:otherwise>
        <div class="row g-4">
            <c:forEach items="${daftarMateri}" var="m">
                <div class="col-md-4">
                    <div class="card h-100 shadow-sm border-0" style="border-radius: 1.25rem; transition: transform 0.2s ease, box-shadow 0.2s ease;" onmouseover="this.style.transform='translateY(-5px)'; this.style.boxShadow='0 10px 25px -5px rgba(37,99,235,0.1)';" onmouseout="this.style.transform='none'; this.style.boxShadow='0 4px 6px -1px rgba(0,0,0,0.05)';">
                        <div class="card-body p-4 d-flex flex-column">
                            <div class="d-flex justify-content-between align-items-start mb-3">
                                <h5 class="card-title fw-bold mb-0 text-dark" style="font-size: 1.1rem; line-height: 1.4;">${m.namaMateri}</h5>
                                <c:choose>
                                    <c:when test="${m.jenjang == 'SD'}"><span class="badge bg-info text-white rounded-pill px-3 py-2 fw-semibold" style="font-size: 0.7rem; letter-spacing: 0.5px;">SD</span></c:when>
                                    <c:when test="${m.jenjang == 'SMP'}"><span class="badge bg-warning text-dark rounded-pill px-3 py-2 fw-semibold" style="font-size: 0.7rem; letter-spacing: 0.5px;">SMP</span></c:when>
                                    <c:when test="${m.jenjang == 'SMA'}"><span class="badge bg-success rounded-pill px-3 py-2 fw-semibold" style="font-size: 0.7rem; letter-spacing: 0.5px;">SMA</span></c:when>
                                    <c:when test="${m.jenjang == 'SMK'}"><span class="badge bg-primary rounded-pill px-3 py-2 fw-semibold" style="font-size: 0.7rem; letter-spacing: 0.5px;">SMK</span></c:when>
                                    <c:otherwise><span class="badge bg-secondary rounded-pill px-3 py-2 fw-semibold" style="font-size: 0.7rem; letter-spacing: 0.5px;">${m.jenjang}</span></c:otherwise>
                                </c:choose>
                            </div>
                            
                            <div class="mb-3">
                                <div class="d-flex align-items-center mb-1 text-secondary" style="font-size: 0.875rem;">
                                    <i class="bi bi-journal-bookmark text-primary me-2"></i> ${m.namaMapel}
                                </div>
                                <div class="d-flex align-items-center text-secondary" style="font-size: 0.875rem;">
                                    <i class="bi bi-mortarboard text-primary me-2"></i> Kelas ${m.kelas} ${not empty m.jurusan and m.jurusan != '(NULL)' ? '- '.concat(m.jurusan) : ''}
                                </div>
                            </div>
                            
                            <p class="card-text text-secondary mb-4 flex-grow-1" style="font-size: 0.875rem; line-height: 1.6;">
                                <c:choose>
                                    <c:when test="${fn:length(m.deskripsi) > 80}">
                                        ${fn:substring(m.deskripsi, 0, 80)}...
                                    </c:when>
                                    <c:otherwise>
                                        ${m.deskripsi}
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            
                            <a href="${pageContext.request.contextPath}/materi/detail?id=${m.idMateri}" class="btn btn-outline-primary w-100 mt-auto" style="border-radius: 0.75rem; font-weight: 600; padding: 0.6rem; transition: all 0.2s;">
                                Lihat Detail
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
