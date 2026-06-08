<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="id">
<head>
    <meta charset="UTF-8">
    <title>Halaman Tidak Ditemukan - HUMANA</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="bg-light d-flex align-items-center justify-content-center" style="min-height: 100vh;">
    <div class="text-center p-5 bg-white shadow-sm rounded-4" style="max-width: 500px;">
        <h1 class="display-1 fw-bold text-primary mb-3">404</h1>
        <h4 class="fw-bold text-dark mb-3">Oops! Halaman Tidak Ditemukan</h4>
        <p class="text-secondary mb-4">Maaf, halaman yang Anda cari mungkin telah dipindahkan atau tidak pernah ada.</p>
        <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary rounded-pill px-4 py-2 fw-semibold">Kembali ke Dashboard</a>
    </div>
</body>
</html>
