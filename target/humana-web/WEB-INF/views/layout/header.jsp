<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty pageTitle ? 'HUMANA' : pageTitle} - Platform Les Privat</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<nav class="navbar navbar-expand-lg fixed-top bg-white shadow-sm border-bottom" style="border-color: #E2E8F0;">
    <div class="container">
        <a class="navbar-brand fw-bold text-primary" href="${pageContext.request.contextPath}/" style="color: #2563EB !important; letter-spacing: 1px;">HUMANA</a>
        
        <c:if test="${not empty sessionScope.userId}">
            <button class="navbar-toggler border-0 shadow-none" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain" aria-controls="navbarMain" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarMain">
                <ul class="navbar-nav mx-auto mb-2 mb-lg-0 fw-semibold text-secondary">
                    <c:choose>
                        <c:when test="${sessionScope.userRole == 'MURID'}">
                            <li class="nav-item">
                                <a class="nav-link px-3 ${activePage == 'dashboard' ? 'active text-primary' : ''}" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link px-3 ${activePage == 'pesan' ? 'active text-primary' : ''}" href="${pageContext.request.contextPath}/pesan">Pesan Sesi</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link px-3 ${activePage == 'jadwal' ? 'active text-primary' : ''}" href="${pageContext.request.contextPath}/jadwal">Jadwal Aktif</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link px-3 ${activePage == 'riwayat' ? 'active text-primary' : ''}" href="${pageContext.request.contextPath}/histori">Riwayat</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link px-3 ${activePage == 'materi' ? 'active text-primary' : ''}" href="${pageContext.request.contextPath}/materi">Materi</a>
                            </li>
                        </c:when>
                        <c:when test="${sessionScope.userRole == 'GURU'}">
                            <li class="nav-item">
                                <a class="nav-link px-3 ${activePage == 'dashboard' ? 'active text-primary' : ''}" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link px-3 ${activePage == 'jadwal' ? 'active text-primary' : ''}" href="${pageContext.request.contextPath}/jadwal">Permintaan</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link px-3 ${activePage == 'jadwal' ? 'active text-primary' : ''}" href="${pageContext.request.contextPath}/jadwal">Jadwal</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link px-3 ${activePage == 'riwayat' ? 'active text-primary' : ''}" href="${pageContext.request.contextPath}/histori">Riwayat</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link px-3 ${activePage == 'materi' ? 'active text-primary' : ''}" href="${pageContext.request.contextPath}/materi">Materi</a>
                            </li>
                        </c:when>
                    </c:choose>
                </ul>
                
                <div class="d-flex align-items-center gap-3 mt-3 mt-lg-0">
                    <div class="text-end d-none d-lg-block">
                        <div class="fw-bold text-dark lh-1" style="font-size: 0.95rem;">${sessionScope.userName}</div>
                        <span class="badge bg-light text-secondary border mt-1" style="font-size: 0.7rem;">${sessionScope.userRole}</span>
                    </div>
                    <form action="${pageContext.request.contextPath}/auth/logout" method="post" class="m-0">
                        <button type="submit" class="btn btn-outline-danger btn-sm rounded-pill px-3 fw-semibold">Logout</button>
                    </form>
                </div>
            </div>
        </c:if>
    </div>
</nav>

<div class="container main-content-padding">
