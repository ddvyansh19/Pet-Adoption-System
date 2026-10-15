<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1>Pet listings</h1>
        <p class="muted">Approve or reject the listings submitted by shelters. Only approved pets are visible to adopters.</p>
    </div>
</div>

<div class="tabs">
    <a class="${empty status ? 'active' : ''}" href="${ctx}/admin/pets">All</a>
    <a class="${status == 'PENDING' ? 'active' : ''}" href="${ctx}/admin/pets?status=PENDING">Pending</a>
    <a class="${status == 'APPROVED' ? 'active' : ''}" href="${ctx}/admin/pets?status=APPROVED">Approved</a>
    <a class="${status == 'REJECTED' ? 'active' : ''}" href="${ctx}/admin/pets?status=REJECTED">Rejected</a>
</div>

<div class="card">
    <c:choose>
        <c:when test="${empty pets}">
            <div class="empty"><div class="big">📭</div>No listings in this view.</div>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
            <table>
                <thead><tr><th>Pet</th><th>Shelter</th><th>Details</th><th>Listing</th><th>Adoption</th><th></th></tr></thead>
                <tbody>
                <c:forEach items="${pets}" var="p">
                    <tr>
                        <td class="cell-pet"><t:thumb photo="${p.photoPath}" type="${p.type}" size="md"/>
                            <span><strong><c:out value="${p.name}"/></strong><br><span class="muted small"><c:out value="${p.type}"/> · <c:out value="${p.breed}"/></span></span></td>
                        <td><c:out value="${p.shelterName}"/><br><span class="muted small">📍 <c:out value="${p.location}"/></span></td>
                        <td style="max-width:260px"><span class="small"><c:out value="${p.ageLabel}"/>. <c:out value="${p.description}"/></span></td>
                        <td><t:status value="${p.approvalStatus}"/></td>
                        <td><t:status value="${p.adoptionStatus}"/></td>
                        <td class="right">
                            <form class="inline" method="post" action="${ctx}/admin/pets">
                                <t:csrf/>
                                <input type="hidden" name="id" value="${p.id}">
                                <input type="hidden" name="returnStatus" value="${status}">
                                <c:if test="${!p.approved}"><button class="btn btn-sm btn-ok" name="action" value="approve" type="submit">Approve</button></c:if>
                                <c:if test="${!p.rejected}"><button class="btn btn-sm btn-danger" name="action" value="reject" type="submit">Reject</button></c:if>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
