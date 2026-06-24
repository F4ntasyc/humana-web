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
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <style>
        /* ===== CSS Variables (Premium Glassmorphism) ===== */
        :root {
            --brand-navy: #2B4C7E;
            --brand-navy-dark: #1E365C;
            --brand-accent: #3A7D6B;
            
            --glass-bg: rgba(255, 255, 255, 0.03);
            --glass-border: rgba(255, 255, 255, 0.1);
            --glass-highlight: rgba(255, 255, 255, 0.05);
            
            --text-main: #FFFFFF;
            --text-muted: rgba(255, 255, 255, 0.6);
            
            --input-bg: rgba(255, 255, 255, 0.03);
            --input-border: rgba(255, 255, 255, 0.1);
            --input-focus: rgba(255, 255, 255, 0.25);
        }

        /* ===== Reset & Base ===== */
        * { margin: 0; padding: 0; box-sizing: border-box; }
        html, body { height: 100%; overflow: hidden; }

        body {
            font-family: 'DM Sans', sans-serif;
            background-color: var(--brand-navy-dark);
            color: var(--text-main);
            -webkit-font-smoothing: antialiased;
        }

        a { text-decoration: none; color: inherit; }

        /* ===== Full Screen Layout ===== */
        .app-container {
            display: flex;
            flex-direction: column;
            height: 100vh;
            position: relative;
            z-index: 1;
        }

        /* ===== Ambient Light Sources ===== */
        .ambient-lights {
            position: absolute;
            inset: 0;
            overflow: hidden;
            z-index: 0;
            pointer-events: none;
        }
        .light-orb {
            position: absolute;
            border-radius: 50%;
            filter: blur(100px);
            opacity: 0.5;
            animation: orbFloat 20s infinite alternate ease-in-out;
        }
        .orb-1 {
            width: 50vw; height: 50vw;
            background: var(--brand-navy);
            top: -10%; left: -10%;
        }
        .orb-2 {
            width: 40vw; height: 40vw;
            background: rgba(58, 125, 107, 0.15); /* Accent glow */
            bottom: -10%; right: -5%;
            animation-delay: -5s;
        }
        .orb-3 {
            width: 30vw; height: 30vw;
            background: rgba(255, 255, 255, 0.05);
            top: 40%; left: 50%;
            transform: translate(-50%, -50%);
        }

        @keyframes orbFloat {
            0% { transform: translate(0, 0) scale(1); }
            100% { transform: translate(5%, 10%) scale(1.1); }
        }

        /* ===== Navbar ===== */
        .top-nav {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1.25rem 3rem;
            position: relative;
            z-index: 10;
        }
        .brand-logo {
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }
        .brand-logo img { height: 28px; }
        .brand-name {
            font-size: 1.25rem;
            font-weight: 700;
            letter-spacing: -0.5px;
        }
        .nav-links { display: flex; gap: 2rem; }
        .nav-links a {
            font-size: 0.875rem;
            color: var(--text-muted);
            font-weight: 500;
            transition: color 0.3s;
        }
        .nav-links a:hover { color: var(--text-main); }

        /* ===== Main Split Content ===== */
        .split-layout {
            flex: 1;
            display: flex;
            position: relative;
            z-index: 10;
            overflow: hidden; /* Important for scroll layout */
        }

        /* --- Left Side: Branding --- */
        .hero-section {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 0 4rem;
            position: relative;
        }
        
        .hero-content {
            max-width: 480px;
            animation: fadeUp 1s ease-out;
        }

        .icon-glass-box {
            width: 80px; height: 80px;
            background: var(--glass-bg);
            border: 1px solid var(--glass-border);
            backdrop-filter: blur(12px);
            border-radius: 24px;
            display: flex; align-items: center; justify-content: center;
            margin-bottom: 2rem;
            box-shadow: 0 8px 32px rgba(0,0,0,0.1);
        }
        .icon-glass-box img {
            width: 40px; height: 40px;
            filter: brightness(0) invert(1);
        }

        .hero-title {
            font-size: 3rem;
            font-weight: 700;
            line-height: 1.1;
            margin-bottom: 1.25rem;
            letter-spacing: -1px;
        }
        .hero-title span {
            background: linear-gradient(135deg, #FFF, rgba(255,255,255,0.5));
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
        .hero-title .accent {
            background: linear-gradient(135deg, var(--brand-accent), #52A68F);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .hero-subtitle {
            font-size: 1.125rem;
            color: var(--text-muted);
            line-height: 1.6;
        }

        @keyframes fadeUp {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        /* Floating particles */
        .particles { position: absolute; inset: 0; pointer-events: none; }
        .particle {
            position: absolute; width: 3px; height: 3px;
            background: rgba(255,255,255,0.2); border-radius: 50%;
            animation: floatUp 15s linear infinite;
        }
        .particle:nth-child(1) { left: 20%; top: 80%; animation-duration: 12s; }
        .particle:nth-child(2) { left: 60%; top: 90%; animation-duration: 18s; animation-delay: -5s; width: 4px; height: 4px; }
        .particle:nth-child(3) { left: 80%; top: 70%; animation-duration: 15s; animation-delay: -2s; }
        
        @keyframes floatUp {
            0% { transform: translateY(0); opacity: 0; }
            20% { opacity: 1; }
            80% { opacity: 1; }
            100% { transform: translateY(-500px); opacity: 0; }
        }

        /* --- Right Side: Form --- */
        .auth-section {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem 2rem;
            overflow: hidden;
        }

        .glass-card {
            width: 100%;
            max-width: 420px;
            background: #FFFFFF;
            border: none;
            border-radius: 20px;
            padding: 1.5rem 1.75rem;
            box-shadow: 0 20px 50px rgba(0,0,0,0.3);
            animation: fadeLeft 0.8s cubic-bezier(0.16, 1, 0.3, 1);
            color: #1E293B;
        }

        @keyframes fadeLeft {
            from { opacity: 0; transform: translateX(30px); }
            to { opacity: 1; transform: translateX(0); }
        }

        .card-header { margin-bottom: 1rem; text-align: center; }
        .card-title {
            font-size: 1.25rem;
            font-weight: 700;
            margin-bottom: 0.2rem;
            letter-spacing: -0.5px;
            color: #1E293B;
        }
        .card-subtitle {
            font-size: 0.8rem;
            color: #64748B;
        }
        .card-subtitle a {
            color: var(--brand-accent);
            font-weight: 600;
            text-decoration: none;
            transition: color 0.3s;
        }
        .card-subtitle a:hover { color: var(--brand-navy); text-decoration: underline; }

        /* Role Toggle */
        .role-toggle {
            display: flex;
            background: #F1F5F9;
            border: 1.5px solid #E2E8F0;
            border-radius: 10px;
            padding: 3px;
            margin-bottom: 1rem;
            gap: 3px;
        }
        .role-btn {
            flex: 1;
            padding: 0.45rem 1rem;
            font-family: 'DM Sans', sans-serif;
            font-size: 0.8rem;
            font-weight: 600;
            color: #64748B;
            background: transparent;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.4rem;
        }
        .role-btn.active {
            background: var(--brand-accent);
            color: #FFFFFF;
            box-shadow: 0 4px 12px rgba(58, 125, 107, 0.3);
        }
        .role-btn:not(.active):hover {
            color: #1E293B;
            background: #E2E8F0;
        }

        /* Form Controls */
        .input-group { margin-bottom: 0.65rem; }
        .input-group label {
            display: block;
            font-size: 0.65rem;
            font-weight: 600;
            color: #64748B;
            margin-bottom: 0.25rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .input-wrapper { position: relative; }
        .input-wrapper i.icon-left {
            position: absolute; left: 0.85rem; top: 50%; transform: translateY(-50%);
            color: #94A3B8; font-size: 1rem; pointer-events: none;
            transition: color 0.3s;
        }
        .glass-input {
            width: 100%;
            background: #F8FAFC;
            border: 1.5px solid #E2E8F0;
            color: #1E293B;
            font-family: 'DM Sans', sans-serif;
            font-size: 0.85rem;
            padding: 0.6rem 0.85rem 0.6rem 2.25rem;
            border-radius: 10px;
            transition: all 0.3s;
            outline: none;
        }
        .glass-input::placeholder { color: #94A3B8; }
        .glass-input:hover { background: #F1F5F9; }
        .glass-input:focus {
            background: #FFFFFF;
            border-color: var(--brand-navy);
            box-shadow: 0 0 0 3px rgba(43, 76, 126, 0.1);
        }
        .glass-input:focus ~ i.icon-left { color: var(--brand-navy); }

        .btn-icon-right {
            position: absolute; right: 0.75rem; top: 50%; transform: translateY(-50%);
            background: none; border: none; color: #94A3B8;
            cursor: pointer; font-size: 1rem; transition: color 0.3s;
            display: flex; align-items: center; justify-content: center; padding: 0.2rem;
        }
        .btn-icon-right:hover { color: var(--brand-navy); }

        /* Password Match Text */
        .password-match {
            font-size: 0.7rem;
            margin-top: 0.3rem;
            display: none;
            align-items: center;
            gap: 0.25rem;
        }
        .password-match.match {
            display: flex;
            color: #6EE7B7; /* Green for dark mode */
        }
        .password-match.mismatch {
            display: flex;
            color: #FCA5A5; /* Red for dark mode */
        }

        /* Primary Button */
        .btn-primary {
            width: 100%;
            background: var(--brand-accent);
            color: #FFFFFF;
            border: none;
            padding: 0.75rem;
            border-radius: 10px;
            font-family: 'DM Sans', sans-serif;
            font-size: 0.9rem;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.3s;
            display: flex; justify-content: center; align-items: center; gap: 0.5rem;
            box-shadow: 0 4px 12px rgba(58, 125, 107, 0.3);
            margin-top: 0.75rem;
        }
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(58, 125, 107, 0.4);
        }

        /* Alerts */
        .glass-alert {
            background: #FEF2F2;
            border: 1px solid #FECACA;
            color: #DC2626;
            padding: 0.5rem 0.75rem;
            border-radius: 10px;
            font-size: 0.8rem;
            display: flex; align-items: center; gap: 0.5rem;
            margin-bottom: 1rem;
        }
        .glass-alert.success {
            background: #F0FDF4;
            border-color: #BBF7D0;
            color: #16A34A;
        }

        /* Footer */
        .bottom-footer {
            display: flex; justify-content: space-between; align-items: center;
            padding: 1rem 3rem;
            position: relative; z-index: 10;
        }
        .footer-text { font-size: 0.75rem; color: rgba(255,255,255,0.4); }
        .footer-links { display: flex; gap: 1.5rem; }
        .footer-links a { font-size: 0.75rem; color: rgba(255,255,255,0.4); transition: color 0.3s; }
        .footer-links a:hover { color: var(--text-main); }

        /* Responsive */
        @media (max-width: 992px) {
            .split-layout { flex-direction: column; overflow-y: auto; }
            .hero-section { padding: 3rem 2rem; flex: 0 0 auto; align-items: center; text-align: center; }
            .hero-title { font-size: 2.25rem; }
            .auth-section { padding: 1rem; align-items: center; overflow-y: visible; }
            .glass-card { padding: 1.5rem 1.5rem; }
            .top-nav, .bottom-footer { padding: 1rem 1.5rem; flex-shrink: 0; }
            .bottom-footer { flex-direction: column; gap: 1rem; }
        }
        
        /* Thin Scrollbar for auth section if needed */
        .auth-section::-webkit-scrollbar { width: 6px; }
        .auth-section::-webkit-scrollbar-track { background: transparent; }
        .auth-section::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 10px; }
        .auth-section::-webkit-scrollbar-thumb:hover { background: rgba(255,255,255,0.2); }
    </style>
</head>
<body>

<div class="ambient-lights">
    <div class="light-orb orb-1"></div>
    <div class="light-orb orb-2"></div>
    <div class="light-orb orb-3"></div>
</div>

<div class="app-container">
    <!-- Navbar -->
    <nav class="top-nav">
        <div class="brand-logo">
            <img src="${pageContext.request.contextPath}/assets/images/LOGOH.png?v=3" alt="Logo">
            <div class="brand-name">HUMANA.</div>
        </div>
        <div class="nav-links">
            <a href="#">Tentang</a>
            <a href="#">Bantuan</a>
        </div>
    </nav>

    <!-- Main Content -->
    <main class="split-layout">
        
        <!-- Left: Branding -->
        <section class="hero-section">
            <div class="particles">
                <div class="particle"></div>
                <div class="particle"></div>
                <div class="particle"></div>
            </div>
            
            <div class="hero-content">
                <div class="icon-glass-box">
                    <img src="${pageContext.request.contextPath}/assets/images/LOGOH.png?v=3" alt="Icon">
                </div>
                <h1 class="hero-title">
                    <span>Humanity in action,</span><br>
                    <span>Learning in motion.</span>
                </h1>
                <p class="hero-subtitle">
                    Platform ekosistem pendidikan cerdas yang menghubungkan pelajar dan pengajar privat berkualitas untuk masa depan gemilang.
                </p>
            </div>
        </section>

        <!-- Right: Auth Form -->
        <section class="auth-section">
            <div class="glass-card">
                <div class="card-header">
                    <h2 class="card-title">Buat Akun</h2>
                    <p class="card-subtitle">
                        Sudah punya akun? 
                        <a href="${pageContext.request.contextPath}/auth/login">Masuk di sini</a>
                    </p>
                </div>

                <c:if test="${not empty error}">
                    <div class="glass-alert">
                        <i class="bi bi-exclamation-circle"></i>
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

                <form method="post" action="${pageContext.request.contextPath}/auth/register" id="registerForm" novalidate>
                    <input type="hidden" name="role" id="roleInput" value="Murid">

                    <div class="input-group">
                        <label for="namaLengkap">Nama Lengkap</label>
                        <div class="input-wrapper">
                            <input type="text" id="namaLengkap" name="namaLengkap" class="glass-input" placeholder="Masukkan nama lengkap" required autocomplete="name">
                            <i class="bi bi-person icon-left"></i>
                        </div>
                    </div>

                    <div class="input-group">
                        <label for="email">Alamat Email</label>
                        <div class="input-wrapper">
                            <input type="email" id="email" name="email" class="glass-input" placeholder="contoh@email.com" required autocomplete="email">
                            <i class="bi bi-envelope icon-left"></i>
                        </div>
                    </div>

                    <div class="input-group">
                        <label for="password">Kata Sandi</label>
                        <div class="input-wrapper">
                            <input type="password" id="password" name="password" class="glass-input" placeholder="Minimal 6 karakter" required autocomplete="new-password" minlength="6">
                            <i class="bi bi-lock icon-left"></i>
                            <button type="button" class="btn-icon-right" id="togglePassword">
                                <i class="bi bi-eye-slash" id="togglePassIcon"></i>
                            </button>
                        </div>
                    </div>

                    <div class="input-group">
                        <label for="konfirmasi">Konfirmasi Sandi</label>
                        <div class="input-wrapper">
                            <input type="password" id="konfirmasi" name="konfirmasi" class="glass-input" placeholder="Ulangi kata sandi" required autocomplete="new-password">
                            <i class="bi bi-shield-lock icon-left"></i>
                            <button type="button" class="btn-icon-right" id="toggleKonfirmasi">
                                <i class="bi bi-eye-slash" id="toggleKonfIcon"></i>
                            </button>
                        </div>
                        <div class="password-match" id="passwordMatch">
                            <i class="bi" id="matchIcon"></i>
                            <span id="matchText"></span>
                        </div>
                    </div>

                    <button type="submit" class="btn-primary" id="btnRegister">
                        Daftar Sekarang <i class="bi bi-arrow-right"></i>
                    </button>
                </form>
            </div>
        </section>
    </main>

    <!-- Footer -->
    <footer class="bottom-footer">
        <div class="footer-text">&copy; 2024 Humana. Hak cipta dilindungi.</div>
        <div class="footer-links">
            <a href="#">Privasi</a>
            <a href="#">Persyaratan</a>
        </div>
    </footer>
</div>

<script>
    // Role Toggle
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

    // Password Toggles
    function setupToggle(toggleBtnId, inputId, iconId) {
        document.getElementById(toggleBtnId).addEventListener('click', function () {
            const input = document.getElementById(inputId);
            const icon = document.getElementById(iconId);
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.replace('bi-eye-slash', 'bi-eye');
            } else {
                input.type = 'password';
                icon.classList.replace('bi-eye', 'bi-eye-slash');
            }
        });
    }
    setupToggle('togglePassword', 'password', 'togglePassIcon');
    setupToggle('toggleKonfirmasi', 'konfirmasi', 'toggleKonfIcon');

    // Password Match Validation
    const passwordInput = document.getElementById('password');
    const konfirmasiInput = document.getElementById('konfirmasi');
    const matchDiv = document.getElementById('passwordMatch');
    const matchIcon = document.getElementById('matchIcon');
    const matchText = document.getElementById('matchText');

    function checkPasswordMatch() {
        const pass = passwordInput.value;
        const konf = konfirmasiInput.value;

        if (konf.length === 0) {
            matchDiv.style.display = 'none';
            return;
        }

        matchDiv.style.display = 'flex';
        if (pass === konf) {
            matchDiv.className = 'password-match match';
            matchIcon.className = 'bi bi-check-circle-fill';
            matchText.textContent = 'Kata sandi cocok';
        } else {
            matchDiv.className = 'password-match mismatch';
            matchIcon.className = 'bi bi-x-circle-fill';
            matchText.textContent = 'Kata sandi tidak cocok';
        }
    }

    passwordInput.addEventListener('input', checkPasswordMatch);
    konfirmasiInput.addEventListener('input', checkPasswordMatch);

    // Form Submit Validation
    document.getElementById('registerForm').addEventListener('submit', function (e) {
        const pass = passwordInput.value;
        const konf = konfirmasiInput.value;

        if (pass !== konf) {
            e.preventDefault();
            matchDiv.style.display = 'flex';
            matchDiv.className = 'password-match mismatch';
            matchIcon.className = 'bi bi-x-circle-fill';
            matchText.textContent = 'Kata sandi tidak cocok — perbaiki sebelum mendaftar';
            konfirmasiInput.focus();
        }
    });
</script>
</body>
</html>
