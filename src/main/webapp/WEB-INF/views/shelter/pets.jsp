<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1>My pets</h1>
        <p class="muted">New listings are reviewed by an admin before adopters can see them.</p>
    </div>
    <a class="btn" href="${ctx}/shelter/pets?action=new">+ List a pet</a>
</div>

<c:choose>
    <c:when test="${empty pets}">
        <div class="card empty"><div class="big">🐾</div>You have no pet listings yet.</div>
    </c:when>
    <c:otherwise>
        <div class="card">
            <div class="table-wrap">
            <table>
                <thead><tr><th>Pet</th><th>Age</th><th>Location</th><th>Listing</th><th>Adoption</th><th></th></tr></thead>
                <tbody>
                <c:forEach items="${pets}" var="p">
                    <tr>
                        <td class="cell-pet"><t:thumb photo="${p.photoPath}" type="${p.type}" size="md"/>
                            <span><strong><c:out value="${p.name}"/></strong><br><span class="muted small"><c:out value="${p.type}"/> · <c:out value="${p.breed}"/></span></span></td>
                        <td><c:out value="${p.ageLabel}"/></td>
                        <td><c:out value="${p.location}"/></td>
                        <td><t:status value="${p.approvalStatus}"/></td>
                        <td><t:status value="${p.adoptionStatus}"/></td>
                        <td class="right">
                            <a class="btn btn-sm btn-secondary" href="${ctx}/shelter/pets?action=edit&id=${p.id}">Edit</a>
                            <form class="inline" method="post" action="${ctx}/shelter/pets"
                                  onsubmit="return confirm('Remove this listing and its applications?');">
                                <t:csrf/>
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${p.id}">
                                <button class="btn btn-sm btn-danger" type="submit">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
