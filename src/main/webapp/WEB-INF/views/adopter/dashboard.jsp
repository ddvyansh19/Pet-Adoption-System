<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1>Hi, <c:out value="${sessionScope.user.name}"/> 👋</h1>
        <p class="muted"><c:out value="${sessionScope.user.roleDescription}"/></p>
    </div>
    <a class="btn" href="${ctx}/adopter/browse">Browse pets</a>
</div>

<div class="stats">
    <div class="stat"><div class="num">${stats.total_applications}</div><div class="lbl">Applications submitted</div></div>
    <div class="stat"><div class="num">${stats.pending_applications}</div><div class="lbl">Waiting for a decision</div></div>
    <div class="stat"><div class="num">${stats.approved_applications}</div><div class="lbl">Approved</div></div>
    <div class="stat"><div class="num">${stats.rejected_applications}</div><div class="lbl">Not approved</div></div>
</div>

<div class="card">
    <h2>Recent applications <a class="btn btn-sm btn-secondary" href="${ctx}/adopter/applications">View all</a></h2>
    <c:choose>
        <c:when test="${empty recentApplications}">
            <div class="empty"><div class="big">🐕</div>You have not applied for any pet yet.<br><br>
                <a class="btn" href="${ctx}/adopter/browse">Find your new best friend</a></div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
            <table>
                <thead><tr><th>ID</th><th>Pet</th><th>Shelter</th><th>Applied</th><th>Status</th></tr></thead>
                <tbody>
                <c:forEach items="${recentApplications}" var="a">
                    <tr>
                        <td>#${a.id}</td>
                        <td class="cell-pet"><t:thumb photo="${a.petPhoto}" size="sm"/><strong><c:out value="${a.petName}"/></strong></td>
                        <td><c:out value="${a.shelterName}"/></td>
                        <td><fmt:formatDate value="${a.appliedAt}" pattern="dd MMM yyyy"/></td>
                        <td><t:status value="${a.status}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
