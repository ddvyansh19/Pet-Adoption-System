<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
</main>
<footer>
    <div class="container">
        &copy; <c:out value="${empty siteName ? 'Paws & Homes' : siteName}"/> &middot; Adopt, don't shop
        <c:if test="${not empty contactEmail}"> &middot; Contact: <c:out value="${contactEmail}"/></c:if>
    </div>
</footer>
</body>
</html>
