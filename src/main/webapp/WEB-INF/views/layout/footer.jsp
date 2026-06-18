<c:if test="${not empty sessionScope.userId}">
        </div> <!-- end content-area -->
    </main> <!-- end main-wrapper -->
</c:if>
<c:if test="${empty sessionScope.userId}">
    </div> <!-- end fallback container -->
</c:if>

<!-- Bootstrap JS Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<!-- Custom JS -->
<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>
