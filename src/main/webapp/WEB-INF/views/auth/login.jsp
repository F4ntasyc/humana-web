<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Masuk — HUMANA</title>
    <meta name="description" content="Masuk ke akun HUMANA untuk mengelola les privat Anda.">
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

        /* ===== Ambient Light Sources (For Glassmorphism) ===== */
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
            overflow: hidden;
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
            font-size: 1.35rem;
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

        /* Form Controls */
        .input-group { margin-bottom: 0.75rem; }
        .input-group label {
            display: block;
            font-size: 0.7rem;
            font-weight: 600;
            color: #64748B;
            margin-bottom: 0.3rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .input-wrapper { position: relative; }
        .input-wrapper i.icon-left {
            position: absolute; left: 0.875rem; top: 50%; transform: translateY(-50%);
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
            padding: 0.6rem 1rem 0.6rem 2.5rem;
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
        }
        .btn-icon-right:hover { color: var(--brand-navy); }

        /* Options */
        .form-options {
            display: flex; justify-content: space-between; align-items: center;
            margin-bottom: 1rem;
        }
        .checkbox-wrapper {
            display: flex; align-items: center; gap: 0.4rem; cursor: pointer;
        }
        .checkbox-wrapper input {
            appearance: none; width: 14px; height: 14px;
            border: 1.5px solid #CBD5E1; border-radius: 4px;
            background: transparent; cursor: pointer; position: relative;
            transition: all 0.2s;
        }
        .checkbox-wrapper input:checked {
            background: var(--brand-navy); border-color: var(--brand-navy);
        }
        .checkbox-wrapper input:checked::after {
            content: '✓'; position: absolute; color: #FFFFFF;
            font-size: 9px; font-weight: 900; top: 50%; left: 50%;
            transform: translate(-50%, -50%);
        }
        .checkbox-wrapper span { font-size: 0.8rem; color: #64748B; }
        
        .link-forgot {
            font-size: 0.8rem; color: var(--brand-accent); font-weight: 500; transition: color 0.3s;
        }
        .link-forgot:hover { color: var(--brand-navy); }

        /* Buttons */
        .btn-primary {
            width: 100%;
            background: var(--brand-accent);
            color: #FFFFFF;
            border: none;
            padding: 0.7rem;
            border-radius: 10px;
            font-family: 'DM Sans', sans-serif;
            font-size: 0.9rem;
            font-weight: 700;
            cursor: pointer;
            transition: all 0.3s;
            display: flex; justify-content: center; align-items: center; gap: 0.5rem;
            box-shadow: 0 4px 12px rgba(58, 125, 107, 0.3);
        }
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px rgba(58, 125, 107, 0.4);
        }

        .btn-social {
            width: 100%;
            background: #FFFFFF;
            border: 1.5px solid #E2E8F0;
            color: #1E293B;
            padding: 0.6rem;
            border-radius: 10px;
            font-family: 'DM Sans', sans-serif;
            font-size: 0.85rem;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s;
            display: flex; justify-content: center; align-items: center; gap: 0.5rem;
            margin-top: 0.5rem;
        }
        .btn-social:hover {
            background: #F8FAFC;
            border-color: #CBD5E1;
        }
        .btn-social svg { width: 18px; height: 18px; flex-shrink: 0; }

        /* Divider */
        .divider {
            display: flex; align-items: center; gap: 0.75rem; margin: 0.875rem 0 0.25rem;
        }
        .divider::before, .divider::after {
            content: ''; flex: 1; height: 1px; background: #E2E8F0;
        }
        .divider span {
            font-size: 0.7rem; color: #94A3B8; text-transform: uppercase;
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
                    <span class="accent">Learning in motion.</span>
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
                    <h2 class="card-title">Masuk ke Akun</h2>
                    <p class="card-subtitle">
                        Belum memiliki akun? 
                        <a href="${pageContext.request.contextPath}/auth/register">Daftar sekarang</a>
                    </p>
                </div>

                <c:if test="${param.sukses == '1'}">
                    <div class="glass-alert success">
                        <i class="bi bi-check-circle"></i>
                        Registrasi berhasil. Silakan masuk.
                    </div>
                </c:if>

                <c:if test="${not empty error}">
                    <div class="glass-alert">
                        <i class="bi bi-exclamation-circle"></i>
                        ${error}
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/auth/login" id="loginForm">
                    <div class="input-group">
                        <label for="email">Alamat Email atau Username</label>
                        <div class="input-wrapper">
                            <input type="text" id="email" name="email" class="glass-input" placeholder="Email atau username Anda" required autocomplete="username">
                            <i class="bi bi-person icon-left"></i>
                        </div>
                    </div>

                    <div class="input-group">
                        <label for="password">Kata Sandi</label>
                        <div class="input-wrapper">
                            <input type="password" id="password" name="password" class="glass-input" placeholder="Masukkan kata sandi" required autocomplete="current-password">
                            <i class="bi bi-lock icon-left"></i>
                            <button type="button" class="btn-icon-right" id="togglePassword">
                                <i class="bi bi-eye-slash" id="toggleIcon"></i>
                            </button>
                        </div>
                    </div>

                    <div class="form-options">
                        <label class="checkbox-wrapper">
                            <input type="checkbox" name="remember" id="rememberMe">
                            <span>Ingat saya di perangkat ini</span>
                        </label>
                        <a href="#" class="link-forgot">Lupa sandi?</a>
                    </div>

                    <button type="submit" class="btn-primary">
                        Masuk Sekarang <i class="bi bi-arrow-right"></i>
                    </button>
                </form>

                <div class="divider"><span>Atau lanjutkan dengan</span></div>

                <button type="button" class="btn-social">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 01-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z" fill="#4285F4"/>
                        <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                        <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
                        <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
                    </svg>
                    Google
                </button>
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
    document.getElementById('togglePassword').addEventListener('click', function () {
        const input = document.getElementById('password');
        const icon = document.getElementById('toggleIcon');
        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.replace('bi-eye-slash', 'bi-eye');
        } else {
            input.type = 'password';
            icon.classList.replace('bi-eye', 'bi-eye-slash');
        }
    });
</script>
</body>
</html>
