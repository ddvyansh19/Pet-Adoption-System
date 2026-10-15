<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1>Adoption applications</h1>
        <p class="muted">Approving an application marks the pet as adopted and closes the other pending applications for it.</p>
    </div>
</div>

<div class="tabs">
    <a class="${empty status ? 'active' : ''}" href="${ctx}/shelter/applications">All</a>
    <a class="${status == 'PENDING' ? 'active' : ''}" href="${ctx}/shelter/applications?status=PENDING">Pending</a>
    <a class="${status == 'APPROVED' ? 'active' : ''}" href="${ctx}/shelter/applications?status=APPROVED">Approved</a>
    <a class="${status == 'REJECTED' ? 'active' : ''}" href="${ctx}/shelter/applications?status=REJECTED">Rejected</a>
</div>

<c:choose>
    <c:when test="${empty applications}">
        <div class="card empty"><div class="big">📬</div>No applications in this view.</div>
    </c:when>
    <c:otherwise>
        <c:forEach items="${applications}" var="a">
            <div class="app-card">
                <div class="head">
                    <div class="cell-pet">
                        <t:thumb photo="${a.petPhoto}" size="sm"/>
                        <div>
                            <strong>#${a.id} · <c:out value="${a.petName}"/></strong><br>
                            <span class="muted small">from <c:out value="${a.adopterName}"/> (<c:out value="${a.adopterEmail}"/>) · <fmt:formatDate value="${a.appliedAt}" pattern="dd MMM yyyy, HH:mm"/></span>
                        </div>
                    </div>
                    <t:status value="${a.status}"/>
                </div>
                <dl class="detail-list" style="margin:.5rem 0">
                    <dt>Why adopt?</dt><dd><c:out value="${a.reason}"/></dd>
                    <dt>Housing</dt><dd><c:out value="${a.housingType}"/></dd>
                    <dt>Other pets</dt><dd><c:out value="${a.hasOtherPets ? 'Yes' : 'No'}"/></dd>
                    <c:if test="${not empty a.experience}"><dt>Experience</dt><dd><c:out value="${a.experience}"/></dd></c:if>
                    <c:if test="${not empty a.shelterNote}"><dt>Your note</dt><dd><c:out value="${a.shelterNote}"/></dd></c:if>
                </dl>
                <c:if test="${a.pending}">
                    <form method="post" action="${ctx}/shelter/applications">
                        <t:csrf/>
                        <input type="hidden" name="id" value="${a.id}">
                        <input type="hidden" name="returnStatus" value="${status}">
                        <label for="note${a.id}" style="margin-top:0">Note to the adopter (optional)</label>
                        <input type="text" id="note${a.id}" name="note" maxlength="500" placeholder="e.g. Please visit us this weekend to meet the pet">
                        <div class="form-actions">
                            <button class="btn btn-ok" name="action" value="approve" type="submit"
                                    onclick="return confirm('Approve this application? The pet will be marked as adopted.');">Approve</button>
                            <button class="btn btn-danger" name="action" value="reject" type="submit">Reject</button>
                        </div>
                    </form>
                </c:if>
            </div>
        </c:forEach>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
