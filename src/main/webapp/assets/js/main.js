/**
 * Format angka ke mata uang Rupiah
 * @param {number|string} angka 
 * @returns {string} format RpXXX.XXX
 */
function formatRupiah(angka) {
    if (!angka) return "Rp0";
    let val = parseInt(angka);
    if (isNaN(val)) return "Rp0";
    return new Intl.NumberFormat('id-ID', { 
        style: 'currency', 
        currency: 'IDR', 
        minimumFractionDigits: 0 
    }).format(val);
}

/**
 * Hitung estimasi biaya sesi (dipakai di pesan.jsp)
 * Durasi menit di-ceil ke kelipatan 1 jam (60 mnt), lalu dikali 30.000
 */
function hitungEstimasiBiaya() {
    const elMulai = document.getElementById('waktuMulai');
    const elSelesai = document.getElementById('waktuSelesai');
    const elEstimasi = document.getElementById('estimasiBiayaSesi');
    
    if (!elMulai || !elSelesai || !elEstimasi) return;
    
    const waktuMulaiStr = elMulai.value;
    const waktuSelesaiStr = elSelesai.value;

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
        elEstimasi.textContent = "Rp0";
        elEstimasi.classList.remove('text-danger');
        elEstimasi.classList.add('text-primary');
    }
}

/**
 * Inisialisasi Star Rating modal
 */
function initStarRating() {
    // Fungsi ini sebenarnya opsional karena styling star rating menggunakan CSS flex-row-reverse 
    // dan selector ~ di CSS sudah cukup menangani interaksi warna bintang.
    // Tapi jika diperlukan tambahan logic bisa di sini.
}

/**
 * Tampilkan Alert dinamis menggunakan DOM
 * @param {string} pesan 
 * @param {string} tipe 'success'|'danger'|'warning'
 */
function showAlert(pesan, tipe = 'success') {
    const container = document.querySelector('.main-content-padding') || document.body;
    
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${tipe} alert-dismissible fade show`;
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        ${pesan}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    container.insertBefore(alertDiv, container.firstChild);
    
    // Auto remove setelah 5 detik
    setTimeout(() => {
        if(alertDiv.parentNode) {
            const bsAlert = new bootstrap.Alert(alertDiv);
            bsAlert.close();
        }
    }, 5000);
}
