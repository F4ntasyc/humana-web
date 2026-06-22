/**
 * Submit POST form secara dinamis (dipakai di jadwal-aktif.jsp)
 */
function submitForm(url, params) {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = url;
    Object.entries(params).forEach(([key, value]) => {
        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = key;
        input.value = value;
        form.appendChild(input);
    });
    document.body.appendChild(form);
    form.submit();
}

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
