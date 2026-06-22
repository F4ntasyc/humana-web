<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Pesan Sesi Belajar" />
<c:set var="activePage" value="pesan" />
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<%-- Tom Select CSS for searchable dropdown --%>
<link href="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/css/tom-select.bootstrap5.min.css" rel="stylesheet">

<style>
    .ts-control { border-radius: 0.75rem !important; border: 1px solid #DEE2E6 !important; font-size: 0.95rem; }
    .ts-control:focus-within { border-color: #0d6efd !important; box-shadow: 0 0 0 0.25rem rgba(13,110,253,.25) !important; }
    .ts-dropdown { border-radius: 0.75rem !important; border: 1px solid #DEE2E6 !important; }
    .time-select-disabled { background-color: #F8FAFC; color: #ADB5BD; }
    .time-row { display: flex; gap: 1rem; }
    .time-row > div { flex: 1; }
    .date-row { margin-bottom: 1rem; }
</style>

<div class="page-header mb-4">
    <h1 class="fw-bold" style="color: #1E293B;">Pesan Sesi Belajar</h1>
    <p class="text-secondary">Silakan isi form di bawah ini untuk melakukan pemesanan sesi belajar.</p>
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
                    <option value="">-- Cari atau Pilih Materi --</option>
                    <c:forEach items="${daftarMateri}" var="m">
                        <option value="${m.idMateri}" ${m.idMateri == selectedMateri ? 'selected' : ''}>
                            ${m.namaMateri} - ${m.namaMapel} (Kelas ${m.kelas})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <h5 class="fw-bold mb-3 mt-4 pt-3 border-top" style="color: #1E293B;">2. Jadwal</h5>

            <%-- Baris 1: Tanggal --%>
            <div class="date-row">
                <label for="tanggalSesi" class="form-label fw-semibold text-secondary" style="font-size: 0.875rem;">Tanggal Sesi <span class="text-danger">*</span></label>
                <input type="date" class="form-input form-control" id="tanggalSesi" name="tanggalSesi" required
                       min="${pageContext.request.servletContext.getAttribute('today')}"
                       onchange="updateWaktuSelesai()" style="border-radius: 0.75rem; max-width: 320px;">
            </div>

            <%-- Baris 2: Waktu Mulai & Waktu Selesai --%>
            <div class="time-row mb-4">
                <div>
                    <label for="waktuMulaiTime" class="form-label fw-semibold text-secondary" style="font-size: 0.875rem;">Waktu Mulai <span class="text-danger">*</span></label>
                    <select id="waktuMulaiTime" name="waktuMulaiTime" class="form-select form-input" required onchange="updateWaktuSelesai()" style="border-radius: 0.75rem;">
                        <option value="">-- Pilih jam mulai --</option>
                    </select>
                </div>
                <div>
                    <label for="waktuSelesaiTime" class="form-label fw-semibold text-secondary" style="font-size: 0.875rem;">Waktu Selesai <span class="text-danger">*</span></label>
                    <select id="waktuSelesaiTime" name="waktuSelesaiTime" class="form-select form-input time-select-disabled" required onchange="hitungEstimasiBiaya()" style="border-radius: 0.75rem;" disabled>
                        <option value="">-- Pilih waktu mulai dulu --</option>
                    </select>
                </div>
            </div>

            <%-- Hidden inputs gabungan datetime--%>
            <input type="hidden" id="waktuMulai" name="waktuMulai">
            <input type="hidden" id="waktuSelesai" name="waktuSelesai">

            <h5 class="fw-bold mb-3 mt-4 pt-3 border-top" style="color: #1E293B;">3. Lokasi</h5>
            <div class="mb-4">
                <label for="lokasiSesi" class="form-label fw-semibold text-secondary" style="font-size: 0.875rem;">Alamat Lengkap <span class="text-danger">*</span></label>
                <textarea class="form-input form-control" id="lokasiSesi" name="lokasiSesi" rows="4" placeholder="Masukkan alamat lengkap (termasuk kelurahan, kecamatan, dll)" required style="border-radius: 0.75rem;"></textarea>
            </div>

            <h5 class="fw-bold mb-3 mt-4 pt-3 border-top" style="color: #1E293B;">4. Estimasi Biaya</h5>
            <div class="mb-4 p-4 rounded-3" style="background-color: #F8FAFC; border: 1px solid #E2E8F0;">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="text-secondary fw-semibold">Biaya Sesi (<span id="durasiLabel">0</span> sesi × Rp 15.000):</span>
                    <span class="fw-bold fs-5 text-primary" id="estimasiBiayaSesi">Rp 0</span>
                </div>
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span class="text-secondary fw-semibold">Estimasi Biaya Transport (±3 km × Rp 3.000):</span>
                    <span class="fw-bold text-secondary">Rp 9.000</span>
                </div>
                <hr class="my-2">
                <div class="d-flex justify-content-between align-items-center">
                    <span class="fw-bold text-dark">Estimasi Total:</span>
                    <span class="fw-bold fs-5" style="color: #1E365C;" id="estimasiTotal">Rp 9.000</span>
                </div>
                <div class="text-muted mt-2" style="font-size: 0.8rem;">
                    * Biaya transport aktual dihitung oleh guru setelah mengkonfirmasi pesanan.
                </div>
            </div>

            <div class="text-end pt-3">
                <button type="submit" class="btn btn-primary px-4 py-2 shadow-sm" id="btnKirim" disabled style="border-radius: 0.75rem; font-weight: 600; background: linear-gradient(135deg, #2563EB 0%, #1D4ED8 100%); border: none; opacity: 0.6; cursor: not-allowed;">
                    <i class="bi bi-send me-2"></i> Kirim Permintaan
                </button>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/tom-select@2.3.1/dist/js/tom-select.complete.min.js"></script>
<script>
    // ===== Tom Select: Searchable Materi Dropdown =====
    new TomSelect('#idMateri', {
        placeholder: 'Cari atau pilih materi...',
        searchField: ['text'],
        maxOptions: 200,
        render: {
            no_results: function() {
                return '<div class="no-results">Materi tidak ditemukan</div>';
            }
        }
    });

    // ===== Generate time slots (08:00 to 20:00) =====
    function generateTimeSlots() {
        var slots = [];
        for (var h = 8; h <= 20; h++) {
            for (var m = 0; m < 60; m += 30) {
                if (h === 20 && m > 0) continue; // stop at 20:00
                var hStr = String(h).padStart(2, '0');
                var mStr = String(m).padStart(2, '0');
                slots.push(hStr + ':' + mStr);
            }
        }
        return slots;
    }

    var ALL_SLOTS = generateTimeSlots(); // ["08:00","08:30",..."20:00"]

    // ===== Populate Waktu Mulai select =====
    (function() {
        var sel = document.getElementById('waktuMulaiTime');
        ALL_SLOTS.forEach(function(t) {
            var parts = t.split(':');
            var h = parseInt(parts[0]);
            var m = parseInt(parts[1]);
            if (h === 20 && m === 0) return; // 20:00 cannot be start time if min duration is 1h, but let's allow it if we only have 20:00 as last slot? Wait, min duration 1h means max start time is 19:00 if end is 20:00.
            if (h >= 8 && h <= 19) {
                var opt = document.createElement('option');
                opt.value = t;
                opt.textContent = t;
                sel.appendChild(opt);
            }
        });
    })();

    // ===== Update Waktu Selesai berdasarkan Waktu Mulai =====
    function updateWaktuSelesai() {
        var mulaiTime = document.getElementById('waktuMulaiTime').value;
        var selesaiSel = document.getElementById('waktuSelesaiTime');

        // Reset estimasi
        document.getElementById('estimasiBiayaSesi').textContent = 'Rp 0';
        document.getElementById('estimasiTotal').textContent = 'Rp 9.000';
        document.getElementById('durasiLabel').textContent = '0';

        if (!mulaiTime) {
            selesaiSel.innerHTML = '<option value="">-- Pilih waktu mulai dulu --</option>';
            selesaiSel.disabled = true;
            selesaiSel.classList.add('time-select-disabled');
            updateHiddenInputs();
            return;
        }

        // Cari index waktu mulai
        var mulaiIdx = ALL_SLOTS.indexOf(mulaiTime);
        if (mulaiIdx < 0) return;

        // Waktu selesai minimal = waktu mulai + 1 jam (2 slot)
        var minSelesaiIdx = mulaiIdx + 2;

        selesaiSel.innerHTML = '<option value="">-- Pilih waktu selesai --</option>';
        for (var i = minSelesaiIdx; i < ALL_SLOTS.length; i++) {
            var opt = document.createElement('option');
            opt.value = ALL_SLOTS[i];
            opt.textContent = ALL_SLOTS[i];
            selesaiSel.appendChild(opt);
        }

        selesaiSel.disabled = false;
        selesaiSel.classList.remove('time-select-disabled');
        
        // Auto-select opsi pertama yang valid (durasi minimal 1 jam)
        if (selesaiSel.options.length > 1) {
            selesaiSel.selectedIndex = 1;
        } else {
            selesaiSel.value = '';
        }

        updateHiddenInputs();
        hitungEstimasiBiaya();
    }

    // ===== Update hidden datetime inputs =====
    function updateHiddenInputs() {
        var tanggal = document.getElementById('tanggalSesi').value;
        var mulaiTime = document.getElementById('waktuMulaiTime').value;
        var selesaiTime = document.getElementById('waktuSelesaiTime').value;

        document.getElementById('waktuMulai').value = (tanggal && mulaiTime) ? tanggal + 'T' + mulaiTime : '';
        document.getElementById('waktuSelesai').value = (tanggal && selesaiTime) ? tanggal + 'T' + selesaiTime : '';

        checkFormReady();
    }

    // ===== Hitung Estimasi Biaya =====
    // Rp 15.000 per 30 menit, Rp 3.000/km estimasi 3km
    var BIAYA_PER_30_MENIT = 15000;
    var BIAYA_TRANSPORT_ESTIMASI = 9000; // 3km x 3000

    function hitungEstimasiBiaya() {
        updateHiddenInputs();

        var mulaiTime = document.getElementById('waktuMulaiTime').value;
        var selesaiTime = document.getElementById('waktuSelesaiTime').value;
        var elBiayaSesi = document.getElementById('estimasiBiayaSesi');
        var elTotal = document.getElementById('estimasiTotal');
        var elDurasi = document.getElementById('durasiLabel');

        if (mulaiTime && selesaiTime) {
            var mulaiMin = timeToMinutes(mulaiTime);
            var selesaiMin = timeToMinutes(selesaiTime);
            var diffMenit = selesaiMin - mulaiMin;

            if (diffMenit > 0) {
                var jumlahSesi = diffMenit / 30; // berapa sesi 30 menit
                var biayaSesi = jumlahSesi * BIAYA_PER_30_MENIT;
                var total = biayaSesi + BIAYA_TRANSPORT_ESTIMASI;

                elDurasi.textContent = jumlahSesi;
                elBiayaSesi.textContent = formatRupiah(biayaSesi);
                elTotal.textContent = formatRupiah(total);
                return;
            }
        }
        elDurasi.textContent = '0';
        elBiayaSesi.textContent = 'Rp 0';
        elTotal.textContent = formatRupiah(BIAYA_TRANSPORT_ESTIMASI);
    }

    function timeToMinutes(timeStr) {
        var parts = timeStr.split(':');
        return parseInt(parts[0]) * 60 + parseInt(parts[1]);
    }

    function formatRupiah(angka) {
        return new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', minimumFractionDigits: 0 }).format(angka);
    }

    // ===== Enable submit button only when form is valid =====
    function checkFormReady() {
        var tanggal = document.getElementById('tanggalSesi').value;
        var mulai = document.getElementById('waktuMulaiTime').value;
        var selesai = document.getElementById('waktuSelesaiTime').value;
        var btn = document.getElementById('btnKirim');
        var ready = tanggal && mulai && selesai;
        btn.disabled = !ready;
        btn.style.opacity = ready ? '1' : '0.6';
        btn.style.cursor = ready ? 'pointer' : 'not-allowed';
    }

    // Set tanggal minimum ke hari ini
    (function() {
        var today = new Date();
        var yyyy = today.getFullYear();
        var mm = String(today.getMonth() + 1).padStart(2, '0');
        var dd = String(today.getDate()).padStart(2, '0');
        document.getElementById('tanggalSesi').min = yyyy + '-' + mm + '-' + dd;
    })();

    // Listen perubahan waktu
    document.getElementById('waktuMulaiTime').addEventListener('change', function() {
        updateWaktuSelesai();
    });
    document.getElementById('waktuSelesaiTime').addEventListener('change', function() {
        hitungEstimasiBiaya();
    });
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
