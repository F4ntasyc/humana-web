<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Pesan Sesi Les" />
<c:set var="activePage" value="pesan" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="page-header mb-4">
    <h1 class="fw-bold" style="color: #1E293B;">Pesan Sesi Les</h1>
    <p class="text-secondary">Silakan isi form di bawah ini untuk melakukan pemesanan sesi les privat.</p>
</div>

<c:if test="${not empty error}">
    <div class="alert-custom alert-danger-custom" id="alert-error">
        <i class="bi bi-exclamation-circle-fill"></i>
        ${error}
    </div>
</c:if>

<div class="card shadow-sm border-0" style="border-radius: 1.25rem;">
    <div class="card-body p-4 p-md-5">
        <form action="${pageContext.request.contextPath}/pesan/tambah" method="post" id="formPesan">
            
            <h5 class="fw-bold mb-3 mt-2" style="color: #1E293B;">1. Pilih Materi</h5>
            <div class="mb-4">
                <label for="idMateri" class="form-label fw-semibold text-secondary" style="font-size: 0.875rem;">Materi <span class="text-danger">*</span></label>
                <select name="idMateri" id="idMateri" class="form-select form-input" required style="border-radius: 0.75rem;">
                    <option value="">-- Pilih Materi --</option>
                    <c:forEach items="${daftarMateri}" var="m">
                        <option value="${m.idMateri}" ${m.idMateri == selectedMateri ? 'selected' : ''}>
                            ${m.namaMateri} - ${m.namaMapel} (Kelas ${m.kelas})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <h5 class="fw-bold mb-3 mt-4 pt-3 border-top" style="color: #1E293B;">2. Jadwal</h5>
            <div class="row mb-4">
                <div class="col-md-6 mb-3 mb-md-0">
                    <label for="waktuMulai" class="form-label fw-semibold text-secondary" style="font-size: 0.875rem;">Waktu Mulai <span class="text-danger">*</span></label>
                    <input type="datetime-local" class="form-input form-control" id="waktuMulai" name="waktuMulai" required onchange="hitungEstimasiBiaya()" style="border-radius: 0.75rem;">
                </div>
                <div class="col-md-6">
                    <label for="waktuSelesai" class="form-label fw-semibold text-secondary" style="font-size: 0.875rem;">Waktu Selesai <span class="text-danger">*</span></label>
                    <input type="datetime-local" class="form-input form-control" id="waktuSelesai" name="waktuSelesai" required onchange="hitungEstimasiBiaya()" style="border-radius: 0.75rem;">
                </div>
            </div>

            <h5 class="fw-bold mb-3 mt-4 pt-3 border-top" style="color: #1E293B;">3. Lokasi</h5>
            <div class="mb-4">
                <label for="lokasiSesi" class="form-label fw-semibold text-secondary" style="font-size: 0.875rem;">Alamat Lengkap <span class="text-danger">*</span></label>
                <textarea class="form-input form-control" id="lokasiSesi" name="lokasiSesi" rows="4" placeholder="Masukkan alamat lengkap (termasuk kelurahan, kecamatan, dll)" required style="border-radius: 0.75rem;"></textarea>
            </div>

            <h5 class="fw-bold mb-3 mt-4 pt-3 border-top" style="color: #1E293B;">4. Estimasi Biaya</h5>
            <div class="mb-4 p-4 rounded-3" style="background-color: #F8FAFC; border: 1px solid #E2E8F0;">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="text-secondary fw-semibold">Biaya Sesi:</span>
                    <span class="fw-bold fs-5 text-primary" id="estimasiBiayaSesi">Rp 0</span>
                </div>
                <div class="text-muted" style="font-size: 0.8rem;">
                    * Biaya transport (Rp 3.000/km) akan dihitung dan ditambahkan secara otomatis setelah guru mengkonfirmasi pesanan Anda.
                </div>
            </div>

            <div class="text-end pt-3">
                <button type="submit" class="btn btn-primary px-4 py-2 shadow-sm" style="border-radius: 0.75rem; font-weight: 600; background: linear-gradient(135deg, #2563EB 0%, #1D4ED8 100%); border: none;">
                    <i class="bi bi-send me-2"></i> Kirim Permintaan
                </button>
            </div>
        </form>
    </div>
</div>

<script>
    function formatRupiah(angka) {
        return new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(angka);
    }

    function hitungEstimasiBiaya() {
        const waktuMulaiStr = document.getElementById('waktuMulai').value;
        const waktuSelesaiStr = document.getElementById('waktuSelesai').value;
        const elEstimasi = document.getElementById('estimasiBiayaSesi');

        if (waktuMulaiStr && waktuSelesaiStr) {
            const mulai = new Date(waktuMulaiStr);
            const selesai = new Date(waktuSelesaiStr);

            if (selesai > mulai) {
                const diffMs = selesai - mulai;
                const diffMenit = Math.floor(diffMs / 60000);
                const biayaSesi = Math.ceil(diffMenit / 60.0) * 30000;
                elEstimasi.textContent = formatRupiah(biayaSesi);
                elEstimasi.classList.remove('text-danger');
                elEstimasi.classList.add('text-primary');
            } else {
                elEstimasi.textContent = "Error: Waktu selesai harus setelah waktu mulai";
                elEstimasi.classList.remove('text-primary');
                elEstimasi.classList.add('text-danger');
            }
        } else {
            elEstimasi.textContent = "Rp 0";
            elEstimasi.classList.remove('text-danger');
            elEstimasi.classList.add('text-primary');
        }
    }
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
