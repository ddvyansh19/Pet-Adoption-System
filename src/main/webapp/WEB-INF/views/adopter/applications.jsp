<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1>My applications</h1>
        <p class="muted">Track the status of every adoption application you submitted.</p>
    </div>
</div>

<div class="card">
    <h2>Track by application ID</h2>
    <form class="filter-bar" method="get" action="${ctx}/adopter/applications">
        <div>
            <label for="trackId">Application ID</label>
            <input type="number" id="trackId" name="trackId" min="1" placeholder="e.g. 12" value="<c:out value='${param.trackId}'/>" required>
        </div>
        <div style="flex:0 0 auto"><button class="btn" type="submit">Check status</button></div>
    </form>

    <c:if test="${not empty trackError}">
        <div class="alert alert-error" style="margin-top:1rem"><c:out value="${trackError}"/></div>
    </c:if>
    <c:if test="${not empty tracked}">
        <div class="app-card" style="margin:1rem 0 0">
            <div class="head">
                <strong>Application #${tracked.id} · <c:out value="${tracked.petName}"/></strong>
                <t:status value="${tracked.status}"/>
            </div>
            <p class="muted small">
                Shelter: <c:out value="${tracked.shelterName}"/> ·
                Submitted <fmt:formatDate value="${tracked.appliedAt}" pattern="dd MMM yyyy, HH:mm"/> ·
                Last update <fmt:formatDate value="${tracked.updatedAt}" pattern="dd MMM yyyy, HH:mm"/>
            </p>
            <c:choose>
                <c:when test="${tracked.pending}"><p>The shelter is still reviewing your application.</p></c:when>
                <c:when test="${tracked.approved}"><p>🎉 Congratulations! Your application was approved. The shelter will be in touch.</p></c:when>
                <c:otherwise><p>Unfortunately this application was not approved. Don't lose hope, other pets are waiting for you.</p></c:otherwise>
            </c:choose>
            <c:if test="${not empty tracked.shelterNote}"><p><strong>Note from shelter:</strong> <c:out value="${tracked.shelterNote}"/></p></c:if>
        </div>
    </c:if>
</div>

<div class="card">
    <h2>All applications</h2>
    <c:choose>
        <c:when test="${empty applications}">
            <div class="empty"><div class="big">📝</div>You have not submitted any application yet.<br><br>
                <a class="btn" href="${ctx}/adopter/browse">Browse pets</a></div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
            <table>
                <thead><tr><th>ID</th><th>Pet</th><th>Shelter</th><th>Applied</th><th>Status</th><th></th></tr></thead>
                <tbody>
                <c:forEach items="${applications}" var="a">
                    <tr>
                        <td>#${a.id}</td>
                        <td class="cell-pet"><t:thumb photo="${a.petPhoto}" size="sm"/><strong><c:out value="${a.petName}"/></strong></td>
                        <td><c:out value="${a.shelterName}"/></td>
                        <td><fmt:formatDate value="${a.appliedAt}" pattern="dd MMM yyyy"/></td>
                        <td><t:status value="${a.status}"/></td>
                        <td class="right"><a class="btn btn-sm btn-secondary" href="${ctx}/adopter/applications?trackId=${a.id}">Details</a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
