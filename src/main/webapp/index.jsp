<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Entry point: send visitors to their dashboard or the login page. --%>
<c:choose>
    <c:when test="${not empty sessionScope.user}">
        <c:redirect url="${sessionScope.user.dashboardPath}"/>
    </c:when>
    <c:otherwise>
        <c:redirect url="/login"/>
    </c:otherwise>
</c:choose>
