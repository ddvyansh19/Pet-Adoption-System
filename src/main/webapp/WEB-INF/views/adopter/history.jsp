<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1>Adoption history</h1>
        <p class="muted">Pets you have adopted and your other past applications.</p>
    </div>
</div>

<div class="card">
    <h2>Pets I adopted 💙</h2>
    <c:choose>
        <c:when test="${empty adopted}"><p class="muted">No adopted pets yet. Your first match could be one click away.</p></c:when>
        <c:otherwise>
            <div class="pet-grid">
                <c:forEach items="${adopted}" var="a">
                    <div class="pet-card">
                        <t:thumb photo="${a.petPhoto}" size="lg"/>
                        <div class="pet-card-body">
                            <h3><c:out value="${a.petName}"/></h3>
                            <p class="muted small">Adopted from <c:out value="${a.shelterName}"/></p>
                            <p class="muted small">Approved <fmt:formatDate value="${a.updatedAt}" pattern="dd MMM yyyy"/></p>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<div class="card">
    <h2>Other applications</h2>
    <c:choose>
        <c:when test="${empty otherApplications}"><p class="muted">Nothing here.</p></c:when>
        <c:otherwise>
            <div class="table-wrap">
            <table>
                <thead><tr><th>ID</th><th>Pet</th><th>Applied</th><th>Status</th><th>Shelter note</th></tr></thead>
                <tbody>
                <c:forEach items="${otherApplications}" var="a">
                    <tr>
                        <td>#${a.id}</td>
                        <td><strong><c:out value="${a.petName}"/></strong></td>
                        <td><fmt:formatDate value="${a.appliedAt}" pattern="dd MMM yyyy"/></td>
                        <td><t:status value="${a.status}"/></td>
                        <td><c:out value="${empty a.shelterNote ? '—' : a.shelterNote}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
