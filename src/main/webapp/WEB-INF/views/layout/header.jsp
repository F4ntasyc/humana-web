<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty pageTitle ? 'HUMANA' : pageTitle} - Platform Les Privat</title>
    
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    
    <style>
        :root {
            --humana-navy: #1E365C;
            --humana-navy-dark: #152744;
            --humana-teal: #3A7D6B;
            --bg-light: #F8FAFC;
        }
        body { 
            font-family: 'DM Sans', sans-serif;
            background-color: var(--bg-light);
            margin: 0;
            display: flex;
            flex-direction: row !important;
            height: 100vh;
            overflow: hidden;
            color: #1E293B;
        }
        
        /* Sidebar Styles */
        .sidebar {
            width: 260px;
            background-color: var(--humana-navy);
            color: #FFFFFF;
            display: flex;
            flex-direction: column;
            box-shadow: 2px 0 15px rgba(0,0,0,0.1);
            z-index: 1000;
        }
        .sidebar-brand {
            padding: 1.5rem 1.5rem 2rem;
            display: flex;
            align-items: center;
            gap: 0.75rem;
            text-decoration: none;
            color: white;
        }
        .sidebar-brand img { height: 32px; filter: brightness(0) invert(1); }
        .sidebar-brand span { font-weight: 700; font-size: 1.25rem; letter-spacing: -0.5px; }
        
        .sidebar-subtitle {
            font-size: 0.7rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            color: rgba(255,255,255,0.4);
            padding: 0 1.5rem;
            margin-bottom: 0.5rem;
        }
        
        .nav-menu { flex: 1; display: flex; flex-direction: column; gap: 0.35rem; padding: 0 1rem; }
        .nav-link-item {
            display: flex; align-items: center; gap: 1rem;
            padding: 0.8rem 1rem;
            color: rgba(255,255,255,0.7);
            text-decoration: none;
            border-radius: 12px;
            font-weight: 500;
            font-size: 0.95rem;
            transition: all 0.3s ease;
        }
        .nav-link-item:hover {
            color: white;
            background: rgba(255,255,255,0.05);
        }
        .nav-link-item.active {
            background: var(--humana-teal);
            color: white;
            box-shadow: 0 4px 15px rgba(58, 125, 107, 0.4);
            font-weight: 600;
        }
        .nav-link-item i { font-size: 1.1rem; }
        
        .sidebar-footer { padding: 1.5rem 1rem; border-top: 1px solid rgba(255,255,255,0.1); }

        /* Main Content Layout */
        .main-wrapper {
            flex: 1;
            display: flex;
            flex-direction: column;
            overflow: hidden;
            background-color: var(--bg-light);
        }
        .top-header {
            height: 76px;
            background: white;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 2.5rem;
            border-bottom: 1px solid #E2E8F0;
            z-index: 100;
            flex-shrink: 0;
        }
        .search-bar {
            background: #F1F5F9;
            border-radius: 20px;
            padding: 0.6rem 1.25rem;
            display: flex; align-items: center; gap: 0.75rem;
            width: 350px;
        }
        .search-bar input { border: none; background: transparent; outline: none; width: 100%; font-size: 0.9rem; font-family: 'DM Sans', sans-serif;}
        .search-bar input::placeholder { color: #94A3B8; }
        
        .header-actions { display: flex; align-items: center; gap: 1.75rem; }
        .header-actions a { color: #64748B; text-decoration: none; font-weight: 500; font-size: 0.95rem; transition: color 0.3s; }
        .header-actions a:hover { color: var(--humana-navy); }
        .header-actions i { font-size: 1.25rem; cursor: pointer; color: #64748B; transition: color 0.3s; }
        .header-actions i:hover { color: var(--humana-navy); }
        
        .user-profile { display: flex; align-items: center; gap: 0.85rem; border-left: 1px solid #E2E8F0; padding-left: 1.5rem; }
        .user-info { display: flex; flex-direction: column; align-items: flex-end; line-height: 1.2; }
        .user-name { font-weight: 700; font-size: 0.95rem; color: #1E293B; }
        .user-role { font-size: 0.75rem; color: #64748B; text-transform: uppercase; font-weight: 600; letter-spacing: 0.5px; }
        .user-avatar { 
            width: 40px; height: 40px; border-radius: 50%; 
            background: var(--humana-teal); color: white; 
            display: flex; align-items: center; justify-content: center; 
            font-weight: bold; font-size: 1.1rem; box-shadow: 0 4px 10px rgba(58, 125, 107, 0.3);
        }

        .content-area {
            flex: 1;
            padding: 2.5rem;
            overflow-y: auto;
        }
    </style>
</head>
<body>

<c:if test="${not empty sessionScope.userId}">
    <!-- Sidebar Layout -->
    <aside class="sidebar">
        <!-- Sidebar Brand -->
        <div class="px-4 py-4 d-flex align-items-center justify-content-center border-bottom" style="border-color: rgba(255,255,255,0.1) !important;">
            <a href="${pageContext.request.contextPath}/" class="text-white text-decoration-none d-flex align-items-center gap-2">
                <img src="${pageContext.request.contextPath}/assets/images/LOGOH.png?v=3" alt="Humana Logo" style="height: 36px; width: auto; object-fit: contain;">
                <span class="fs-4 fw-bold" style="letter-spacing: 1px;">HUMANA.</span>
            </a>
        </div>
        
        <div class="sidebar-subtitle mb-2" style="margin-top: 1.5rem;">Menu Utama</div>
        <div class="nav-menu">
            <c:choose>
                <c:when test="${sessionScope.userRole == 'MURID'}">
                    <a href="${pageContext.request.contextPath}/dashboard" class="nav-link-item ${activePage == 'dashboard' ? 'active' : ''}"><i class="bi bi-house-door"></i> Beranda</a>
                    <a href="${pageContext.request.contextPath}/pesan" class="nav-link-item ${activePage == 'pesan' ? 'active' : ''}"><i class="bi bi-calendar-plus"></i> Pesan Sesi</a>
                    <a href="${pageContext.request.contextPath}/jadwal" class="nav-link-item ${activePage == 'jadwal' ? 'active' : ''}"><i class="bi bi-calendar-event"></i> Jadwal Saya</a>
                    <a href="${pageContext.request.contextPath}/materi" class="nav-link-item ${activePage == 'materi' ? 'active' : ''}"><i class="bi bi-journal-text"></i> Materi</a>
                    <a href="${pageContext.request.contextPath}/histori" class="nav-link-item ${activePage == 'riwayat' ? 'active' : ''}"><i class="bi bi-clock-history"></i> Aktivitas</a>
                </c:when>
                <c:when test="${sessionScope.userRole == 'GURU'}">
                    <a href="${pageContext.request.contextPath}/dashboard" class="nav-link-item ${activePage == 'dashboard' ? 'active' : ''}"><i class="bi bi-house-door"></i> Beranda</a>
                    <a href="${pageContext.request.contextPath}/jadwal" class="nav-link-item ${activePage == 'jadwal' ? 'active' : ''}"><i class="bi bi-calendar-event"></i> Jadwal Saya</a>
                    <a href="${pageContext.request.contextPath}/materi" class="nav-link-item ${activePage == 'materi' ? 'active' : ''}"><i class="bi bi-journal-text"></i> Materi</a>
                    <a href="${pageContext.request.contextPath}/histori" class="nav-link-item ${activePage == 'riwayat' ? 'active' : ''}"><i class="bi bi-clock-history"></i> Aktivitas</a>
                </c:when>
            </c:choose>
        </div>
        
        <div class="sidebar-footer">
            <c:if test="${sessionScope.userRole == 'MURID'}">
                <a href="${pageContext.request.contextPath}/profil" class="nav-link-item ${activePage == 'profil' ? 'active' : ''} mb-2"><i class="bi bi-gear"></i> Pengaturan</a>
            </c:if>
            <c:if test="${sessionScope.userRole == 'GURU'}">
                <a href="${pageContext.request.contextPath}/profil" class="nav-link-item ${activePage == 'profil' ? 'active' : ''} mb-2"><i class="bi bi-gear"></i> Pengaturan</a>
            </c:if>
            <form action="${pageContext.request.contextPath}/auth/logout" method="post" class="m-0">
                <button type="submit" class="nav-link-item w-100 border-0 bg-transparent text-start">
                    <i class="bi bi-box-arrow-right"></i> Keluar
                </button>
            </form>
        </div>
    </aside>
    
    <!-- Main Content -->
    <main class="main-wrapper">
        <header class="top-header">
            <div class="search-bar">
                <i class="bi bi-search text-muted"></i>
                <input type="text" placeholder="Cari materi atau jadwal...">
            </div>
            <div class="header-actions">
                <a href="#">Bantuan</a>
                <i class="bi bi-bell"></i>
                <div class="user-profile">
                    <div class="user-info">
                        <span class="user-name">${sessionScope.userName}</span>
                        <span class="user-role">${sessionScope.userRole}</span>
                    </div>
                    <div class="user-avatar">${sessionScope.userName.substring(0,1).toUpperCase()}</div>
                </div>
            </div>
        </header>
        
        <div class="content-area">
</c:if>
<c:if test="${empty sessionScope.userId}">
    <div class="container mt-5">
</c:if>
