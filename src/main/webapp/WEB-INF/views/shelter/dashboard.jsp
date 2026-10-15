<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1>Welcome, <c:out value="${sessionScope.user.name}"/></h1>
        <p class="muted"><c:out value="${sessionScope.user.roleDescription}"/></p>
    </div>
    <div class="row">
        <a class="btn" href="${ctx}/shelter/pets?action=new">+ List a pet</a>
        <a class="btn btn-secondary" href="${ctx}/shelter/applications?status=PENDING">Review applications</a>
    </div>
</div>

<%-- Adoption statistics --%>
<div class="stats">
    <div class="stat"><div class="num">${stats.total_pets}</div><div class="lbl">Pets listed</div></div>
    <div class="stat"><div class="num">${stats.available_pets}</div><div class="lbl">Live and available</div></div>
    <div class="stat"><div class="num">${stats.awaiting_approval}</div><div class="lbl">Awaiting admin approval</div></div>
    <div class="stat"><div class="num">${stats.adopted_pets}</div><div class="lbl">Adopted</div></div>
    <div class="stat"><div class="num">${stats.total_applications}</div><div class="lbl">Applications received</div></div>
    <div class="stat"><div class="num">${adoptionRate}%</div><div class="lbl">Adoption rate</div></div>
</div>

<div class="grid grid-2">
    <div class="card">
        <h2>Application status</h2>
        <c:set var="totalApps" value="${stats.total_applications}"/>
        <c:forEach items="${['PENDING','APPROVED','REJECTED']}" var="st">
            <c:set var="n" value="${st == 'PENDING' ? stats.pending_applications : (st == 'APPROVED' ? stats.approved_applications : stats.rejected_applications)}"/>
            <div style="margin-bottom:.7rem">
                <div class="row spread"><t:status value="${st}"/><strong>${n}</strong></div>
                <div class="bar"><span style="width:<fmt:formatNumber value="${totalApps == 0 ? 0 : n * 100.0 / totalApps}" maxFractionDigits="0"/>%"></span></div>
            </div>
        </c:forEach>
    </div>

    <div class="card">
        <h2>Applications waiting for you <a class="btn btn-sm btn-secondary" href="${ctx}/shelter/applications?status=PENDING">View all</a></h2>
        <c:choose>
            <c:when test="${empty pendingApplications}"><p class="muted">No pending applications right now.</p></c:when>
            <c:otherwise>
                <table>
                    <thead><tr><th>Pet</th><th>Applicant</th><th>Applied</th></tr></thead>
                    <tbody>
                    <c:forEach items="${pendingApplications}" var="a">
                        <tr>
                            <td><strong><c:out value="${a.petName}"/></strong></td>
                            <td><c:out value="${a.adopterName}"/></td>
                            <td><fmt:formatDate value="${a.appliedAt}" pattern="dd MMM"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div class="card">
    <h2>Your latest listings <a class="btn btn-sm btn-secondary" href="${ctx}/shelter/pets">Manage pets</a></h2>
    <c:choose>
        <c:when test="${empty recentPets}">
            <div class="empty"><div class="big">🐕</div>You have not listed any pets yet.<br><br>
                <a class="btn" href="${ctx}/shelter/pets?action=new">List your first pet</a></div>
        </c:when>
        <c:otherwise>
            <table>
                <thead><tr><th>Pet</th><th>Listing</th><th>Adoption</th></tr></thead>
                <tbody>
                <c:forEach items="${recentPets}" var="p">
                    <tr>
                        <td class="cell-pet"><t:thumb photo="${p.photoPath}" type="${p.type}" size="sm"/>
                            <span><strong><c:out value="${p.name}"/></strong><br><span class="muted small"><c:out value="${p.breed}"/></span></span></td>
                        <td><t:status value="${p.approvalStatus}"/></td>
                        <td><t:status value="${p.adoptionStatus}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
