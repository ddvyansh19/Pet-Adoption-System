<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1>Admin dashboard</h1>
        <p class="muted">Platform analytics and everything waiting for your attention.</p>
    </div>
    <div class="row">
        <a class="btn" href="${ctx}/admin/users">Manage users</a>
        <a class="btn btn-secondary" href="${ctx}/admin/settings">System settings</a>
    </div>
</div>

<%-- Platform analytics --%>
<div class="stats">
    <div class="stat"><div class="num">${stats.total_users}</div><div class="lbl">Total users</div></div>
    <div class="stat"><div class="num">${stats.total_shelters}</div><div class="lbl">Shelters</div></div>
    <div class="stat"><div class="num">${stats.total_adopters}</div><div class="lbl">Adopters</div></div>
    <div class="stat"><div class="num">${stats.total_pets}</div><div class="lbl">Pets listed</div></div>
    <div class="stat"><div class="num">${stats.available_pets}</div><div class="lbl">Available to adopt</div></div>
    <div class="stat"><div class="num">${stats.adopted_pets}</div><div class="lbl">Pets adopted</div></div>
    <div class="stat"><div class="num">${stats.total_applications}</div><div class="lbl">Applications</div></div>
    <div class="stat"><div class="num">${adoptionRate}%</div><div class="lbl">Adoption rate</div></div>
</div>

<div class="grid grid-2">
    <%-- Pet listings --%>
    <div class="card">
        <h2>Listings awaiting approval
            <a class="btn btn-sm btn-secondary" href="${ctx}/admin/pets?status=PENDING">View all (${stats.pending_pets})</a>
        </h2>
        <c:choose>
            <c:when test="${empty pendingPets}"><p class="muted">Nothing to review. 🎉</p></c:when>
            <c:otherwise>
                <div class="table-wrap">
                <table>
                    <thead><tr><th>Pet</th><th>Shelter</th><th></th></tr></thead>
                    <tbody>
                    <c:forEach items="${pendingPets}" var="p">
                        <tr>
                            <td class="cell-pet"><t:thumb photo="${p.photoPath}" type="${p.type}" size="sm"/>
                                <span><strong><c:out value="${p.name}"/></strong><br><span class="muted small"><c:out value="${p.breed}"/></span></span></td>
                            <td><c:out value="${p.shelterName}"/></td>
                            <td class="right"><a class="btn btn-sm" href="${ctx}/admin/pets?status=PENDING">Review</a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- User management overview --%>
    <div class="card">
        <h2>Newest users <a class="btn btn-sm btn-secondary" href="${ctx}/admin/users">Manage</a></h2>
        <div class="table-wrap">
        <table>
            <thead><tr><th>Name</th><th>Role</th><th>Joined</th></tr></thead>
            <tbody>
            <c:forEach items="${recentUsers}" var="u">
                <tr>
                    <td><strong><c:out value="${u.name}"/></strong><br><span class="muted small"><c:out value="${u.email}"/></span></td>
                    <td><t:status value="${u.role}"/></td>
                    <td><fmt:formatDate value="${u.createdAt}" pattern="dd MMM yyyy"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        </div>
    </div>

    <%-- Analytics: pets by type --%>
    <div class="card">
        <h2>Approved pets by type</h2>
        <c:choose>
            <c:when test="${empty petsByType}"><p class="muted">No approved pets yet.</p></c:when>
            <c:otherwise>
                <c:forEach items="${petsByType}" var="entry">
                    <div style="margin-bottom:.7rem">
                        <div class="row spread"><span><c:out value="${entry.key}"/></span><strong>${entry.value}</strong></div>
                        <div class="bar"><span style="width:<fmt:formatNumber value="${entry.value * 100.0 / stats.total_pets}" maxFractionDigits="0"/>%"></span></div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- Analytics: top shelters --%>
    <div class="card">
        <h2>Shelters by adoptions</h2>
        <c:choose>
            <c:when test="${empty topShelters}"><p class="muted">No shelters registered yet.</p></c:when>
            <c:otherwise>
                <table>
                    <thead><tr><th>Shelter</th><th class="right">Pets adopted</th></tr></thead>
                    <tbody>
                    <c:forEach items="${topShelters}" var="s">
                        <tr><td><c:out value="${s.name}"/></td><td class="right"><strong>${s.adopted}</strong></td></tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
