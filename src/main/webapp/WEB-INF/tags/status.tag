<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="value" required="true" type="java.lang.Object" %>
<span class="badge badge-${value}"><c:out value="${value}"/></span>
