<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Daftar — HUMANA</title>
    <meta name="description" content="Daftar akun HUMANA sebagai Murid atau Guru les privat.">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <style>
        :root {
            --primary: #2563EB;
            --primary-dark: #1D4ED8;
            --primary-light: #3B82F6;
            --accent: #F97316;
            --bg-gradient-start: #EFF6FF;
            --bg-gradient-end: #DBEAFE;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, var(--bg-gradient-start) 0%, var(--bg-gradient-end) 50%, #EDE9FE 100%);
            position: relative;
            overflow: hidden;
            padding: 0;
        }

        /* Decorative background orbs */
        body::before,
        body::after {
            content: '';
            position: absolute;
            border-radius: 50%;
            filter: blur(80px);
            opacity: 0.4;
            z-index: 0;
        }
        body::before {
            width: 500px;
            height: 500px;
            background: var(--primary);
            top: -150px;
            left: -100px;
        }
        body::after {
            width: 400px;
            height: 400px;
            background: var(--accent);
            bottom: -120px;
            right: -80px;
        }

        .register-container {
            width: 100%;
            max-width: 480px;
            padding: 1rem;
            position: relative;
            z-index: 1;
        }

        /* Logo section */
        .brand-section {
            text-align: center;
            margin-bottom: 0.75rem;
        }
        .brand-logo {
            font-size: 2rem;
            font-weight: 800;
            color: var(--primary);
            letter-spacing: -1px;
            margin-bottom: 0.15rem;
        }
        .brand-logo span {
            color: var(--accent);
        }
        .brand-tagline {
            font-size: 0.8rem;
            color: #64748B;
            font-weight: 400;
        }

        /* Card */
        .register-card {
            background: rgba(255, 255, 255, 0.85);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border: 1px solid rgba(255, 255, 255, 0.6);
            border-radius: 1.25rem;
            padding: 1.5rem;
            box-shadow:
                0 4px 6px -1px rgba(0, 0, 0, 0.05),
                0 20px 50px -12px rgba(37, 99, 235, 0.15);
        }
        .register-card h2 {
            font-size: 1.25rem;
            font-weight: 700;
            color: #1E293B;
            margin-bottom: 0.15rem;
        }
        .register-card .subtitle {
            font-size: 0.8rem;
            color: #94A3B8;
            margin-bottom: 1rem;
        }

        /* Role toggle */
        .role-toggle {
            display: flex;
            background: #F1F5F9;
            border-radius: 0.75rem;
            padding: 4px;
            margin-bottom: 1rem;
            gap: 4px;
        }
        .role-btn {
            flex: 1;
            padding: 0.5rem 1rem;
            font-family: 'Inter', sans-serif;
            font-size: 0.875rem;
            font-weight: 600;
            color: #64748B;
            background: transparent;
            border: none;
            border-radius: 0.6rem;
            cursor: pointer;
            transition: all 0.25s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.4rem;
        }
        .role-btn.active {
            background: var(--primary);
            color: #fff;
            box-shadow: 0 2px 8px rgba(37, 99, 235, 0.3);
        }
        .role-btn:not(.active):hover {
            background: #E2E8F0;
            color: #334155;
        }

        /* Form inputs */
        .form-group {
            margin-bottom: 0.75rem;
        }
        .form-group label {
            display: block;
            font-size: 0.75rem;
            font-weight: 600;
            color: #475569;
            margin-bottom: 0.25rem;
        }
        .input-wrapper {
            position: relative;
        }
        .input-wrapper .input-icon {
            position: absolute;
            left: 0.75rem;
            top: 50%;
            transform: translateY(-50%);
            color: #94A3B8;
            font-size: 1rem;
            pointer-events: none;
            transition: color 0.2s;
        }
        .form-control {
            width: 100%;
            padding: 0.6rem 0.875rem 0.6rem 2.25rem;
            font-family: 'Inter', sans-serif;
            font-size: 0.85rem;
            border: 1.5px solid #E2E8F0;
            border-radius: 0.75rem;
            background: #F8FAFC;
            color: #1E293B;
            transition: all 0.2s ease;
        }
        .form-control:focus {
            outline: none;
            border-color: var(--primary);
            background: #fff;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
        }
        .form-control:focus ~ .input-icon {
            color: var(--primary);
        }

        /* Password toggle */
        .password-toggle {
            position: absolute;
            right: 0.75rem;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: #94A3B8;
            cursor: pointer;
            font-size: 1rem;
            padding: 0.25rem;
            transition: color 0.2s;
        }
        .password-toggle:hover {
            color: var(--primary);
        }

        /* Password match indicator */
        .password-match {
            font-size: 0.7rem;
            margin-top: 0.25rem;
            display: none;
            align-items: center;
            gap: 0.25rem;
        }
        .password-match.match {
            display: flex;
            color: #16A34A;
        }
        .password-match.mismatch {
            display: flex;
            color: #DC2626;
        }

        /* Submit button */
        .btn-register {
            width: 100%;
            padding: 0.7rem;
            font-family: 'Inter', sans-serif;
            font-size: 0.9rem;
            font-weight: 600;
            color: #fff;
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
            border: none;
            border-radius: 0.75rem;
            cursor: pointer;
            transition: all 0.25s ease;
            margin-top: 0.25rem;
        }
        .btn-register:hover {
            transform: translateY(-1px);
            box-shadow: 0 8px 25px -8px rgba(37, 99, 235, 0.5);
        }
        .btn-register:active {
            transform: translateY(0);
        }
        .btn-register:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
            box-shadow: none;
        }

        /* Alert */
        .alert-custom {
            border: none;
            border-radius: 0.75rem;
            padding: 0.5rem 0.75rem;
            font-size: 0.8rem;
            font-weight: 500;
            margin-bottom: 0.75rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        .alert-danger-custom {
            background: #FEF2F2;
            color: #DC2626;
            border-left: 3px solid #DC2626;
        }

        /* Footer link */
        .register-footer {
            text-align: center;
            margin-top: 1rem;
            font-size: 0.875rem;
            color: #64748B;
        }
        .register-footer a {
            color: var(--primary);
            font-weight: 600;
            text-decoration: none;
            transition: color 0.2s;
        }
        .register-footer a:hover {
            color: var(--primary-dark);
            text-decoration: underline;
        }

        /* Animation */
        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
        .register-container {
            animation: fadeInUp 0.6s ease-out;
        }
    </style>
</head>
<body>

<div class="register-container">
    <!-- Brand / Logo -->
    <div class="brand-section">
        <div class="brand-logo">HUMAN<span>A</span></div>
        <div class="brand-tagline">Platform Les Privat Terpercaya</div>
    </div>

    <!-- Register Card -->
    <div class="register-card">
        <h2>Buat Akun Baru</h2>
        <p class="subtitle">Bergabung sebagai murid atau guru les privat</p>

        <%-- Alert: error registrasi --%>
        <c:if test="${not empty error}">
            <div class="alert-custom alert-danger-custom" id="alert-error">
                <i class="bi bi-exclamation-circle-fill"></i>
                ${error}
            </div>
        </c:if>

        <!-- Role Toggle -->
        <div class="role-toggle" id="roleToggle">
            <button type="button" class="role-btn active" id="btnMurid" data-role="Murid">
                <i class="bi bi-mortarboard"></i> Murid
            </button>
            <button type="button" class="role-btn" id="btnGuru" data-role="Guru">
                <i class="bi bi-person-workspace"></i> Guru
            </button>
        </div>

        <form method="post" action="${pageContext.request.contextPath}/auth/register"
              id="registerForm" novalidate>
            <!-- Hidden role -->
            <input type="hidden" name="role" id="roleInput" value="Murid">

            <!-- Nama Lengkap -->
            <div class="form-group">
                <label for="namaLengkap">Nama Lengkap</label>
                <div class="input-wrapper">
                    <input type="text" class="form-control" id="namaLengkap" name="namaLengkap"
                           placeholder="Masukkan nama lengkap" required autocomplete="name">
                    <i class="bi bi-person input-icon"></i>
                </div>
            </div>

            <!-- Email -->
            <div class="form-group">
                <label for="email">Email</label>
                <div class="input-wrapper">
                    <input type="email" class="form-control" id="email" name="email"
                           placeholder="contoh@email.com" required autocomplete="email">
                    <i class="bi bi-envelope input-icon"></i>
                </div>
            </div>

            <!-- Password -->
            <div class="form-group">
                <label for="password">Password</label>
                <div class="input-wrapper">
                    <input type="password" class="form-control" id="password" name="password"
                           placeholder="Minimal 6 karakter" required autocomplete="new-password"
                           minlength="6">
                    <i class="bi bi-lock input-icon"></i>
                    <button type="button" class="password-toggle" id="togglePassword"
                            aria-label="Tampilkan password">
                        <i class="bi bi-eye" id="togglePassIcon"></i>
                    </button>
                </div>
            </div>

            <!-- Konfirmasi Password -->
            <div class="form-group">
                <label for="konfirmasi">Konfirmasi Password</label>
                <div class="input-wrapper">
                    <input type="password" class="form-control" id="konfirmasi" name="konfirmasi"
                           placeholder="Ulangi password" required autocomplete="new-password">
                    <i class="bi bi-shield-lock input-icon"></i>
                    <button type="button" class="password-toggle" id="toggleKonfirmasi"
                            aria-label="Tampilkan konfirmasi password">
                        <i class="bi bi-eye" id="toggleKonfIcon"></i>
                    </button>
                </div>
                <div class="password-match" id="passwordMatch">
                    <i class="bi" id="matchIcon"></i>
                    <span id="matchText"></span>
                </div>
            </div>

            <!-- Submit -->
            <button type="submit" class="btn-register" id="btnRegister">
                <i class="bi bi-person-plus me-1"></i> Daftar
            </button>
        </form>

        <!-- Login link -->
        <div class="register-footer">
            Sudah punya akun?
            <a href="${pageContext.request.contextPath}/auth/login">Masuk</a>
        </div>
    </div>
</div>

<script>
    // ======== Role Toggle ========
    const btnMurid = document.getElementById('btnMurid');
    const btnGuru = document.getElementById('btnGuru');
    const roleInput = document.getElementById('roleInput');

    btnMurid.addEventListener('click', function () {
        btnMurid.classList.add('active');
        btnGuru.classList.remove('active');
        roleInput.value = 'Murid';
    });

    btnGuru.addEventListener('click', function () {
        btnGuru.classList.add('active');
        btnMurid.classList.remove('active');
        roleInput.value = 'Guru';
    });

    // ======== Password Toggle ========
    function setupToggle(toggleBtnId, inputId, iconId) {
        document.getElementById(toggleBtnId).addEventListener('click', function () {
            const input = document.getElementById(inputId);
            const icon = document.getElementById(iconId);
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('bi-eye');
                icon.classList.add('bi-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('bi-eye-slash');
                icon.classList.add('bi-eye');
            }
        });
    }
    setupToggle('togglePassword', 'password', 'togglePassIcon');
    setupToggle('toggleKonfirmasi', 'konfirmasi', 'toggleKonfIcon');

    // ======== Password Match Validation ========
    const passwordInput = document.getElementById('password');
    const konfirmasiInput = document.getElementById('konfirmasi');
    const matchDiv = document.getElementById('passwordMatch');
    const matchIcon = document.getElementById('matchIcon');
    const matchText = document.getElementById('matchText');

    function checkPasswordMatch() {
        const pass = passwordInput.value;
        const konf = konfirmasiInput.value;

        if (konf.length === 0) {
            matchDiv.className = 'password-match';
            return;
        }

        if (pass === konf) {
            matchDiv.className = 'password-match match';
            matchIcon.className = 'bi bi-check-circle-fill';
            matchText.textContent = 'Password cocok';
        } else {
            matchDiv.className = 'password-match mismatch';
            matchIcon.className = 'bi bi-x-circle-fill';
            matchText.textContent = 'Password tidak cocok';
        }
    }

    passwordInput.addEventListener('input', checkPasswordMatch);
    konfirmasiInput.addEventListener('input', checkPasswordMatch);

    // ======== Form Submit Validation ========
    document.getElementById('registerForm').addEventListener('submit', function (e) {
        const pass = passwordInput.value;
        const konf = konfirmasiInput.value;

        if (pass !== konf) {
            e.preventDefault();
            matchDiv.className = 'password-match mismatch';
            matchIcon.className = 'bi bi-x-circle-fill';
            matchText.textContent = 'Password tidak cocok — perbaiki sebelum mendaftar';
            konfirmasiInput.focus();
        }
    });
</script>

</body>
</html>
