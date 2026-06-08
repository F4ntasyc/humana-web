<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Profil" />
<c:set var="pageDescription" value="Kelola profil akun HUMANA Anda" />
<c:set var="activePage" value="profil" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<style>
    /* ===== PROFILE PAGE STYLES ===== */
    .profile-hero {
        background: linear-gradient(135deg, var(--primary) 0%, #1E40AF 100%);
        border-radius: 1.25rem;
        padding: 2.5rem 2rem;
        color: #fff;
        position: relative;
        overflow: hidden;
        margin-bottom: 1.5rem;
    }
    .profile-hero::before {
        content: '';
        position: absolute;
        width: 300px;
        height: 300px;
        background: rgba(255,255,255,0.08);
        border-radius: 50%;
        top: -100px;
        right: -60px;
    }
    .profile-hero::after {
        content: '';
        position: absolute;
        width: 200px;
        height: 200px;
        background: rgba(255,255,255,0.05);
        border-radius: 50%;
        bottom: -80px;
        left: -40px;
    }
    .profile-hero-inner {
        position: relative;
        z-index: 1;
        display: flex;
        align-items: center;
        gap: 1.5rem;
        flex-wrap: wrap;
    }
    .profile-avatar {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        background: rgba(255,255,255,0.2);
        backdrop-filter: blur(10px);
        border: 3px solid rgba(255,255,255,0.4);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.75rem;
        font-weight: 800;
        color: #fff;
        flex-shrink: 0;
    }
    .profile-info h2 {
        font-size: 1.5rem;
        font-weight: 700;
        margin: 0 0 0.25rem;
    }
    .profile-badge {
        display: inline-flex;
        align-items: center;
        gap: 0.3rem;
        padding: 0.2rem 0.75rem;
        border-radius: 999px;
        font-size: 0.6875rem;
        font-weight: 700;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }
    .badge-guru {
        background: rgba(249,115,22,0.2);
        color: #FED7AA;
        border: 1px solid rgba(249,115,22,0.3);
    }
    .badge-murid {
        background: rgba(34,197,94,0.2);
        color: #BBF7D0;
        border: 1px solid rgba(34,197,94,0.3);
    }
    .profile-email {
        font-size: 0.8125rem;
        color: rgba(255,255,255,0.7);
        margin-top: 0.3rem;
    }

    /* Rating stars */
    .rating-stars {
        display: flex;
        align-items: center;
        gap: 0.25rem;
        margin-top: 0.5rem;
    }
    .rating-stars .star {
        color: #FCD34D;
        font-size: 1rem;
    }
    .rating-stars .star.empty {
        color: rgba(255,255,255,0.25);
    }
    .rating-value {
        font-size: 0.875rem;
        font-weight: 600;
        color: rgba(255,255,255,0.9);
        margin-left: 0.4rem;
    }

    /* Tabs */
    .profile-tabs {
        background: #ffffff;
        border-radius: 1.25rem;
        border: 1px solid #E2E8F0;
        overflow: hidden;
        box-shadow: 0 1px 3px rgba(0,0,0,0.04);
    }
    .nav-tabs-custom {
        display: flex;
        border-bottom: 1px solid #E2E8F0;
        padding: 0;
        list-style: none;
        margin: 0;
        overflow-x: auto;
    }
    .nav-tabs-custom li {
        flex-shrink: 0;
    }
    .nav-tab-link {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 1rem 1.5rem;
        font-size: 0.875rem;
        font-weight: 500;
        color: #64748B;
        text-decoration: none;
        border-bottom: 2px solid transparent;
        transition: all 0.2s ease;
        cursor: pointer;
        background: none;
        border-top: none;
        border-left: none;
        border-right: none;
        white-space: nowrap;
    }
    .nav-tab-link:hover {
        color: #2563EB;
        background: rgba(37,99,235,0.05);
    }
    .nav-tab-link.active {
        color: #2563EB;
        border-bottom-color: #2563EB;
        font-weight: 600;
    }
    .nav-tab-link i {
        font-size: 1rem;
    }

    /* Tab content */
    .tab-panel {
        display: none;
        padding: 2rem;
    }
    .tab-panel.active {
        display: block;
    }

    /* Form styles inside profile */
    .form-section-title {
        font-size: 1rem;
        font-weight: 700;
        color: #1E293B;
        margin-bottom: 1.25rem;
        padding-bottom: 0.75rem;
        border-bottom: 1px solid #E2E8F0;
    }
    .form-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 1rem;
        margin-bottom: 1rem;
    }
    .form-row.single {
        grid-template-columns: 1fr;
    }
    .form-group-profile {
        margin-bottom: 1rem;
    }
    .form-group-profile label {
        display: block;
        font-size: 0.8125rem;
        font-weight: 600;
        color: #64748B;
        margin-bottom: 0.4rem;
    }
    .form-input {
        width: 100%;
        padding: 0.7rem 1rem;
        font-family: 'Inter', sans-serif;
        font-size: 0.875rem;
        border: 1.5px solid #E2E8F0;
        border-radius: 0.625rem;
        background: #F8FAFC;
        color: #1E293B;
        transition: all 0.2s ease;
    }
    .form-input:focus {
        outline: none;
        border-color: #2563EB;
        background: #fff;
        box-shadow: 0 0 0 3px rgba(37,99,235,0.1);
    }
    .form-select-profile {
        appearance: none;
        background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%2394A3B8' d='M6 8L1 3h10z'/%3E%3C/svg%3E");
        background-repeat: no-repeat;
        background-position: right 1rem center;
        padding-right: 2.5rem;
    }
    textarea.form-input {
        min-height: 80px;
        resize: vertical;
    }

    /* Toggle switch */
    .toggle-container {
        display: flex;
        align-items: center;
        gap: 1rem;
        padding: 1.25rem;
        background: #F8FAFC;
        border-radius: 0.75rem;
        border: 1.5px solid #E2E8F0;
        margin-bottom: 1rem;
    }
    .toggle-switch {
        position: relative;
        width: 52px;
        height: 28px;
        flex-shrink: 0;
    }
    .toggle-switch input {
        opacity: 0;
        width: 0;
        height: 0;
    }
    .toggle-slider {
        position: absolute;
        cursor: pointer;
        inset: 0;
        background: #CBD5E1;
        border-radius: 999px;
        transition: all 0.3s ease;
    }
    .toggle-slider::before {
        content: '';
        position: absolute;
        height: 22px;
        width: 22px;
        left: 3px;
        bottom: 3px;
        background: #fff;
        border-radius: 50%;
        transition: all 0.3s ease;
        box-shadow: 0 1px 3px rgba(0,0,0,0.15);
    }
    .toggle-switch input:checked + .toggle-slider {
        background: #2563EB;
    }
    .toggle-switch input:checked + .toggle-slider::before {
        transform: translateX(24px);
    }
    .toggle-label {
        flex: 1;
    }
    .toggle-label .toggle-title {
        font-size: 0.875rem;
        font-weight: 600;
        color: #1E293B;
    }
    .toggle-label .toggle-desc {
        font-size: 0.75rem;
        color: #94A3B8;
        margin-top: 0.15rem;
    }

    /* Save button */
    .btn-save {
        padding: 0.7rem 2rem;
        font-family: 'Inter', sans-serif;
        font-size: 0.875rem;
        font-weight: 600;
        color: #fff;
        background: linear-gradient(135deg, #2563EB 0%, #1D4ED8 100%);
        border: none;
        border-radius: 0.625rem;
        cursor: pointer;
        transition: all 0.25s ease;
    }
    .btn-save:hover {
        transform: translateY(-1px);
        box-shadow: 0 6px 20px -6px rgba(37,99,235,0.45);
    }
    .btn-save:active {
        transform: translateY(0);
    }

    @media (max-width: 767.98px) {
        .profile-hero { padding: 1.5rem 1.25rem; }
        .profile-avatar { width: 60px; height: 60px; font-size: 1.25rem; }
        .profile-info h2 { font-size: 1.25rem; }
        .form-row { grid-template-columns: 1fr; }
        .tab-panel { padding: 1.25rem; }
    }
</style>

<%-- Alerts --%>
<c:if test="${param.sukses == '1'}">
    <div class="alert-custom alert-success-custom" id="alert-success">
        <i class="bi bi-check-circle-fill"></i>
        Profil berhasil diperbarui!
    </div>
</c:if>
<c:if test="${not empty param.error}">
    <div class="alert-custom alert-danger-custom" id="alert-error">
        <i class="bi bi-exclamation-circle-fill"></i>
        ${param.error}
    </div>
</c:if>
<c:if test="${not empty error}">
    <div class="alert-custom alert-danger-custom">
        <i class="bi bi-exclamation-circle-fill"></i>
        ${error}
    </div>
</c:if>

<%-- Determine display values based on role --%>
<c:choose>
    <c:when test="${sessionScope.userRole == 'GURU'}">
        <c:set var="displayName" value="${guru.namaUser}" />
        <c:set var="displayEmail" value="${guru.email}" />
        <c:set var="displayUsername" value="${guru.username}" />
        <c:set var="displayPhone" value="${guru.noTelepon}" />
        <c:set var="displayGender" value="${guru.jenisKelamin}" />
        <c:set var="displayAlamat" value="${guru.alamat}" />
    </c:when>
    <c:otherwise>
        <c:set var="displayName" value="${murid.namaUser}" />
        <c:set var="displayEmail" value="${murid.email}" />
        <c:set var="displayUsername" value="${murid.username}" />
        <c:set var="displayPhone" value="${murid.noTelepon}" />
        <c:set var="displayGender" value="${murid.jenisKelamin}" />
        <c:set var="displayAlamat" value="${murid.alamat}" />
    </c:otherwise>
</c:choose>

<%-- Profile Hero Card --%>
<div class="profile-hero">
    <div class="profile-hero-inner">
        <div class="profile-avatar" id="profileAvatar"></div>
        <div class="profile-info">
            <h2 id="profileName">${displayName}</h2>
            <div>
                <c:choose>
                    <c:when test="${sessionScope.userRole == 'GURU'}">
                        <span class="profile-badge badge-guru"><i class="bi bi-person-workspace"></i> Guru</span>
                    </c:when>
                    <c:otherwise>
                        <span class="profile-badge badge-murid"><i class="bi bi-mortarboard"></i> Murid</span>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="profile-email"><i class="bi bi-envelope me-1"></i>${displayEmail}</div>

            <%-- Rating bintang (GURU only) --%>
            <c:if test="${sessionScope.userRole == 'GURU'}">
                <div class="rating-stars" id="ratingStars">
                    <span class="rating-value" id="ratingValue"></span>
                </div>
            </c:if>
        </div>
    </div>
</div>

<%-- Profile Tabs --%>
<div class="profile-tabs">
    <ul class="nav-tabs-custom" role="tablist">
        <li>
            <button class="nav-tab-link active" data-tab="tab-basic" role="tab" id="tabBtnBasic">
                <i class="bi bi-person"></i> Informasi Dasar
            </button>
        </li>
        <c:if test="${sessionScope.userRole == 'MURID'}">
            <li>
                <button class="nav-tab-link" data-tab="tab-academic" role="tab" id="tabBtnAcademic">
                    <i class="bi bi-book"></i> Akademik
                </button>
            </li>
        </c:if>
        <c:if test="${sessionScope.userRole == 'GURU'}">
            <li>
                <button class="nav-tab-link" data-tab="tab-availability" role="tab" id="tabBtnAvailability">
                    <i class="bi bi-calendar-check"></i> Ketersediaan
                </button>
            </li>
        </c:if>
    </ul>

    <%-- ===== TAB 1: Informasi Dasar ===== --%>
    <div class="tab-panel active" id="tab-basic" role="tabpanel">
        <h3 class="form-section-title"><i class="bi bi-pencil-square me-2"></i>Edit Informasi Dasar</h3>

        <form method="post" action="${pageContext.request.contextPath}/profile/update-basic" id="formBasic">
            <div class="form-row">
                <div class="form-group-profile">
                    <label for="nama">Nama Lengkap</label>
                    <input type="text" class="form-input" id="nama" name="nama"
                           value="${displayName}" required placeholder="Masukkan nama lengkap">
                </div>
                <div class="form-group-profile">
                    <label for="username">Username</label>
                    <input type="text" class="form-input" id="username" name="username"
                           value="${displayUsername}" required placeholder="Masukkan username">
                </div>
            </div>

            <div class="form-row">
                <div class="form-group-profile">
                    <label for="noTelepon">No. Telepon</label>
                    <input type="tel" class="form-input" id="noTelepon" name="noTelepon"
                           value="${displayPhone}" placeholder="08xxxxxxxxxx">
                </div>
                <div class="form-group-profile">
                    <label for="jenisKelamin">Jenis Kelamin</label>
                    <select class="form-input form-select-profile" id="jenisKelamin" name="jenisKelamin">
                        <option value="" ${empty displayGender ? 'selected' : ''}>-- Pilih --</option>
                        <option value="L" ${displayGender == 'L' ? 'selected' : ''}>Laki-laki</option>
                        <option value="P" ${displayGender == 'P' ? 'selected' : ''}>Perempuan</option>
                    </select>
                </div>
            </div>

            <div class="form-row single">
                <div class="form-group-profile">
                    <label for="alamat">Alamat</label>
                    <textarea class="form-input" id="alamat" name="alamat"
                              placeholder="Masukkan alamat lengkap">${displayAlamat}</textarea>
                </div>
            </div>

            <button type="submit" class="btn-save" id="btnSaveBasic">
                <i class="bi bi-check-lg me-1"></i> Simpan Perubahan
            </button>
        </form>
    </div>

    <%-- ===== TAB 2: Akademik (MURID only) ===== --%>
    <c:if test="${sessionScope.userRole == 'MURID'}">
        <div class="tab-panel" id="tab-academic" role="tabpanel">
            <h3 class="form-section-title"><i class="bi bi-book me-2"></i>Edit Data Akademik</h3>

            <form method="post" action="${pageContext.request.contextPath}/profile/update-academic" id="formAcademic">
                <div class="form-row">
                    <div class="form-group-profile">
                        <label for="kelas">Kelas</label>
                        <input type="number" class="form-input" id="kelas" name="kelas"
                               min="1" max="12" value="${murid.kelas > 0 ? murid.kelas : ''}"
                               placeholder="1 - 12">
                    </div>
                    <div class="form-group-profile">
                        <label for="jurusan">Jurusan</label>
                        <input type="text" class="form-input" id="jurusan" name="jurusan"
                               value="${murid.jurusan}" placeholder="Contoh: IPA, IPS, dll.">
                    </div>
                </div>

                <button type="submit" class="btn-save" id="btnSaveAcademic">
                    <i class="bi bi-check-lg me-1"></i> Simpan Perubahan
                </button>
            </form>
        </div>
    </c:if>

    <%-- ===== TAB 3: Ketersediaan (GURU only) ===== --%>
    <c:if test="${sessionScope.userRole == 'GURU'}">
        <div class="tab-panel" id="tab-availability" role="tabpanel">
            <h3 class="form-section-title"><i class="bi bi-calendar-check me-2"></i>Status Ketersediaan</h3>

            <form method="post" action="${pageContext.request.contextPath}/profile/update-availability" id="formAvailability">
                <div class="toggle-container">
                    <label class="toggle-switch">
                        <input type="checkbox" id="isActiveToggle"
                               ${guru.active ? 'checked' : ''}>
                        <span class="toggle-slider"></span>
                    </label>
                    <div class="toggle-label">
                        <div class="toggle-title" id="toggleTitle">
                            ${guru.active ? 'Aktif menerima murid' : 'Tidak menerima murid'}
                        </div>
                        <div class="toggle-desc" id="toggleDesc">
                            ${guru.active
                                ? 'Profil Anda akan tampil di daftar guru yang tersedia.'
                                : 'Profil Anda tidak akan tampil di pencarian murid.'}
                        </div>
                    </div>
                </div>

                <%-- Hidden input yang akan di-set via JS --%>
                <input type="hidden" name="isActive" id="isActiveInput"
                       value="${guru.active ? '1' : '0'}">

                <button type="submit" class="btn-save" id="btnSaveAvailability">
                    <i class="bi bi-check-lg me-1"></i> Simpan Perubahan
                </button>
            </form>
        </div>
    </c:if>
</div>

<script>
    // ===== Avatar Initials =====
    (function() {
        var name = document.getElementById('profileName').textContent.trim();
        var initials = '';
        if (name) {
            var parts = name.split(/\s+/);
            initials = parts[0].charAt(0).toUpperCase();
            if (parts.length > 1) initials += parts[parts.length - 1].charAt(0).toUpperCase();
        }
        document.getElementById('profileAvatar').textContent = initials;
    })();

    // ===== Rating Stars (GURU) =====
    (function() {
        var ratingEl = document.getElementById('ratingStars');
        if (!ratingEl) return;

        var ratingVal = parseFloat('${empty rating ? 0 : rating}');
        if (isNaN(ratingVal)) ratingVal = 0;
        var valueEl = document.getElementById('ratingValue');
        var starsHtml = '';

        for (var i = 1; i <= 5; i++) {
            if (i <= Math.round(ratingVal)) {
                starsHtml += '<i class="bi bi-star-fill star"></i>';
            } else {
                starsHtml += '<i class="bi bi-star-fill star empty"></i>';
            }
        }

        ratingEl.insertAdjacentHTML('afterbegin', starsHtml);
        if (ratingVal > 0) {
            valueEl.textContent = ratingVal.toFixed(1) + ' / 5.0';
        } else {
            valueEl.textContent = 'Belum ada rating';
        }
    })();

    // ===== Tab Navigation =====
    document.querySelectorAll('.nav-tab-link').forEach(function(btn) {
        btn.addEventListener('click', function() {
            // Remove active from all tabs and panels
            document.querySelectorAll('.nav-tab-link').forEach(function(b) { b.classList.remove('active'); });
            document.querySelectorAll('.tab-panel').forEach(function(p) { p.classList.remove('active'); });

            // Activate clicked tab
            btn.classList.add('active');
            var targetPanel = document.getElementById(btn.getAttribute('data-tab'));
            if (targetPanel) targetPanel.classList.add('active');
        });
    });

    // ===== Toggle Switch (Guru Availability) =====
    var toggleCheckbox = document.getElementById('isActiveToggle');
    if (toggleCheckbox) {
        toggleCheckbox.addEventListener('change', function() {
            var isActive = this.checked;
            document.getElementById('isActiveInput').value = isActive ? '1' : '0';
            document.getElementById('toggleTitle').textContent =
                isActive ? 'Aktif menerima murid' : 'Tidak menerima murid';
            document.getElementById('toggleDesc').textContent =
                isActive
                    ? 'Profil Anda akan tampil di daftar guru yang tersedia.'
                    : 'Profil Anda tidak akan tampil di pencarian murid.';
        });
    }

    // ===== Auto-dismiss alerts =====
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert-custom');
        alerts.forEach(function(a) {
            a.style.transition = 'opacity 0.5s ease';
            a.style.opacity = '0';
            setTimeout(function() { a.remove(); }, 500);
        });
    }, 4000);
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
