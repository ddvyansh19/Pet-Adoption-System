<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="photo" required="false" type="java.lang.String" %>
<%@ attribute name="type" required="false" type="java.lang.String" %>
<%@ attribute name="size" required="false" type="java.lang.String" %>
<c:choose>
    <c:when test="${not empty photo}">
        <img class="thumb thumb-${empty size ? 'md' : size}" src="${ctx}/photos/<c:out value='${photo}'/>" alt="">
    </c:when>
    <c:otherwise>
        <span class="thumb thumb-${empty size ? 'md' : size} thumb-empty">
            <c:choose>
                <c:when test="${type == 'Dog'}">🐶</c:when>
                <c:when test="${type == 'Cat'}">🐱</c:when>
                <c:when test="${type == 'Rabbit'}">🐰</c:when>
                <c:when test="${type == 'Bird'}">🦜</c:when>
                <c:otherwise>🐾</c:otherwise>
            </c:choose>
        </span>
    </c:otherwise>
</c:choose>
