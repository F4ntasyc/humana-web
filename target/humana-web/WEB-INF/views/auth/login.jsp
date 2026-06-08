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
            overflow-x: hidden;
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
            right: -100px;
        }
        body::after {
            width: 400px;
            height: 400px;
            background: var(--accent);
            bottom: -120px;
            left: -80px;
        }

        .login-container {
            width: 100%;
            max-width: 440px;
            padding: 1.5rem;
            position: relative;
            z-index: 1;
        }

        /* Logo section */
        .brand-section {
            text-align: center;
            margin-bottom: 2rem;
        }
        .brand-logo {
            font-size: 2.5rem;
            font-weight: 800;
            color: var(--primary);
            letter-spacing: -1px;
            margin-bottom: 0.25rem;
        }
        .brand-logo span {
            color: var(--accent);
        }
        .brand-tagline {
            font-size: 0.875rem;
            color: #64748B;
            font-weight: 400;
        }

        /* Card */
        .login-card {
            background: rgba(255, 255, 255, 0.85);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border: 1px solid rgba(255, 255, 255, 0.6);
            border-radius: 1.25rem;
            padding: 2.5rem 2rem;
            box-shadow:
                0 4px 6px -1px rgba(0, 0, 0, 0.05),
                0 20px 50px -12px rgba(37, 99, 235, 0.15);
        }
        .login-card h2 {
            font-size: 1.5rem;
            font-weight: 700;
            color: #1E293B;
            margin-bottom: 0.25rem;
        }
        .login-card .subtitle {
            font-size: 0.875rem;
            color: #94A3B8;
            margin-bottom: 1.75rem;
        }

        /* Form inputs */
        .form-group {
            margin-bottom: 1.25rem;
        }
        .form-group label {
            display: block;
            font-size: 0.8125rem;
            font-weight: 600;
            color: #475569;
            margin-bottom: 0.4rem;
        }
        .input-wrapper {
            position: relative;
        }
        .input-wrapper .input-icon {
            position: absolute;
            left: 0.875rem;
            top: 50%;
            transform: translateY(-50%);
            color: #94A3B8;
            font-size: 1.1rem;
            pointer-events: none;
            transition: color 0.2s;
        }
        .form-control {
            width: 100%;
            padding: 0.75rem 0.875rem 0.75rem 2.75rem;
            font-family: 'Inter', sans-serif;
            font-size: 0.9rem;
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
            right: 0.875rem;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: #94A3B8;
            cursor: pointer;
            font-size: 1.1rem;
            padding: 0.25rem;
            transition: color 0.2s;
        }
        .password-toggle:hover {
            color: var(--primary);
        }

        /* Submit button */
        .btn-login {
            width: 100%;
            padding: 0.8rem;
            font-family: 'Inter', sans-serif;
            font-size: 0.9375rem;
            font-weight: 600;
            color: #fff;
            background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dark) 100%);
            border: none;
            border-radius: 0.75rem;
            cursor: pointer;
            transition: all 0.25s ease;
            position: relative;
            overflow: hidden;
        }
        .btn-login:hover {
            transform: translateY(-1px);
            box-shadow: 0 8px 25px -8px rgba(37, 99, 235, 0.5);
        }
        .btn-login:active {
            transform: translateY(0);
        }

        /* Alert styling */
        .alert-custom {
            border: none;
            border-radius: 0.75rem;
            padding: 0.75rem 1rem;
            font-size: 0.8125rem;
            font-weight: 500;
            margin-bottom: 1.25rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        .alert-danger-custom {
            background: #FEF2F2;
            color: #DC2626;
            border-left: 3px solid #DC2626;
        }
        .alert-success-custom {
            background: #F0FDF4;
            color: #16A34A;
            border-left: 3px solid #16A34A;
        }

        /* Footer link */
        .login-footer {
            text-align: center;
            margin-top: 1.75rem;
            font-size: 0.875rem;
            color: #64748B;
        }
        .login-footer a {
            color: var(--primary);
            font-weight: 600;
            text-decoration: none;
            transition: color 0.2s;
        }
        .login-footer a:hover {
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
        .login-container {
            animation: fadeInUp 0.6s ease-out;
        }
    </style>
</head>
<body>

<div class="login-container">
    <!-- Brand / Logo -->
    <div class="brand-section">
        <div class="brand-logo">HUMAN<span>A</span></div>
        <div class="brand-tagline">Platform Les Privat Terpercaya</div>
    </div>

    <!-- Login Card -->
    <div class="login-card">
        <h2>Selamat Datang</h2>
        <p class="subtitle">Masuk ke akun Anda untuk melanjutkan</p>

        <%-- Alert: registrasi berhasil --%>
        <c:if test="${param.sukses == '1'}">
            <div class="alert-custom alert-success-custom" id="alert-success">
                <i class="bi bi-check-circle-fill"></i>
                Registrasi berhasil! Silakan masuk dengan akun Anda.
            </div>
        </c:if>

        <%-- Alert: error login --%>
        <c:if test="${not empty error}">
            <div class="alert-custom alert-danger-custom" id="alert-error">
                <i class="bi bi-exclamation-circle-fill"></i>
                ${error}
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/auth/login" id="loginForm">
            <!-- Email -->
            <div class="form-group">
                <label for="email">Email atau Username</label>
                <div class="input-wrapper">
                    <input type="text" class="form-control" id="email" name="email"
                           placeholder="contoh@email.com" required autocomplete="email">
                    <i class="bi bi-envelope input-icon"></i>
                </div>
            </div>

            <!-- Password -->
            <div class="form-group">
                <label for="password">Password</label>
                <div class="input-wrapper">
                    <input type="password" class="form-control" id="password" name="password"
                           placeholder="Masukkan password" required autocomplete="current-password">
                    <i class="bi bi-lock input-icon"></i>
                    <button type="button" class="password-toggle" id="togglePassword"
                            aria-label="Tampilkan password">
                        <i class="bi bi-eye" id="toggleIcon"></i>
                    </button>
                </div>
            </div>

            <!-- Submit -->
            <button type="submit" class="btn-login" id="btnLogin">
                <i class="bi bi-box-arrow-in-right me-1"></i> Masuk
            </button>
        </form>

        <!-- Register link -->
        <div class="login-footer">
            Belum punya akun?
            <a href="${pageContext.request.contextPath}/auth/register">Daftar</a>
        </div>
    </div>
</div>

<script>
    // Toggle password visibility
    document.getElementById('togglePassword').addEventListener('click', function () {
        const passwordInput = document.getElementById('password');
        const toggleIcon = document.getElementById('toggleIcon');
        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            toggleIcon.classList.remove('bi-eye');
            toggleIcon.classList.add('bi-eye-slash');
        } else {
            passwordInput.type = 'password';
            toggleIcon.classList.remove('bi-eye-slash');
            toggleIcon.classList.add('bi-eye');
        }
    });
</script>

</body>
</html>
