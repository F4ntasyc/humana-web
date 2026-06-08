@echo off
echo ========================================================
echo   Membangun dan Menjalankan HUMANA Web App (Tomcat 10)
echo ========================================================
echo.
echo Sedang mendownload dependensi dan melakukan build...
echo Silakan tunggu sampai muncul tulisan "Tomcat 10.x started"
echo.
echo Akses Web di: http://localhost:8081/humana-web/auth/login
echo (Untuk menghentikan server, tekan Ctrl+C pada terminal ini)
echo ========================================================
echo.

mvn clean package org.codehaus.cargo:cargo-maven3-plugin:1.10.13:run
