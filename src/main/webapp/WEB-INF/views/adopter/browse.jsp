<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1>Find a pet</h1>
        <p class="muted">Only pets approved by our team and still waiting for a home are shown.</p>
    </div>
</div>

<div class="card">
    <form class="filter-bar" method="get" action="${ctx}/adopter/browse">
        <div>
            <label for="type">Pet type</label>
            <select id="type" name="type">
                <option value="">Any</option>
                <c:forEach items="${petTypes}" var="pt">
                    <option value="${pt}" ${param.type == pt ? 'selected' : ''}><c:out value="${pt}"/></option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label for="breed">Breed</label>
            <input type="text" id="breed" name="breed" placeholder="e.g. Labrador" value="<c:out value='${param.breed}'/>">
        </div>
        <div>
            <label for="location">Location</label>
            <input type="text" id="location" name="location" placeholder="e.g. Noida" value="<c:out value='${param.location}'/>">
        </div>
        <div style="flex:0 0 auto">
            <button class="btn" type="submit">Search</button>
            <c:if test="${searched}"><a class="btn btn-secondary" href="${ctx}/adopter/browse">Clear</a></c:if>
        </div>
    </form>
</div>

<c:choose>
    <c:when test="${empty pets}">
        <div class="card empty">
            <div class="big">🔍</div>
            <c:choose>
                <c:when test="${searched}">No pets match your search. Try fewer filters.</c:when>
                <c:otherwise>No pets are available right now. Please check back soon.</c:otherwise>
            </c:choose>
        </div>
    </c:when>
    <c:otherwise>
        <p class="muted">${pets.size()} pet(s) found</p>
        <div class="pet-grid">
            <c:forEach items="${pets}" var="p"><t:petCard pet="${p}"/></c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
