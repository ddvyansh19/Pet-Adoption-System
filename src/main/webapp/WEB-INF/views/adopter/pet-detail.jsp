<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<p><a href="${ctx}/adopter/browse">← Back to search</a></p>

<div class="grid grid-2">
    <div class="card">
        <c:choose>
            <c:when test="${not empty pet.photoPath}">
                <img class="hero-photo" src="${ctx}/photos/<c:out value='${pet.photoPath}'/>" alt="Photo of <c:out value='${pet.name}'/>">
            </c:when>
            <c:otherwise><t:thumb type="${pet.type}" size="lg"/></c:otherwise>
        </c:choose>
        <div class="row spread" style="margin-top:1rem">
            <h1 style="margin:0"><c:out value="${pet.name}"/></h1>
            <t:status value="${pet.adoptionStatus}"/>
        </div>
        <dl class="detail-list">
            <dt>Type</dt><dd><c:out value="${pet.type}"/></dd>
            <dt>Breed</dt><dd><c:out value="${pet.breed}"/></dd>
            <dt>Age</dt><dd><c:out value="${pet.ageLabel}"/></dd>
            <dt>Location</dt><dd>📍 <c:out value="${pet.location}"/></dd>
            <dt>Shelter</dt><dd><c:out value="${pet.shelterName}"/></dd>
        </dl>
        <c:if test="${not empty pet.description}">
            <h2>About <c:out value="${pet.name}"/></h2>
            <p style="white-space:pre-wrap"><c:out value="${pet.description}"/></p>
        </c:if>
    </div>

    <div class="card">
        <h2>Adoption application</h2>
        <c:choose>
            <c:when test="${alreadyApplied}">
                <div class="alert alert-info">You have already applied for <c:out value="${pet.name}"/>.
                    Check the status under <a href="${ctx}/adopter/applications">Applications</a>.</div>
            </c:when>
            <c:when test="${!pet.available}">
                <div class="alert alert-info"><c:out value="${pet.name}"/> has found a home. 💙 Browse other pets that are waiting.</div>
            </c:when>
            <c:otherwise>
                <form method="post" action="${ctx}/adopter/applications">
                    <t:csrf/>
                    <input type="hidden" name="petId" value="${pet.id}">
                    <label for="reason" style="margin-top:0">Why would you like to adopt <c:out value="${pet.name}"/>?</label>
                    <textarea id="reason" name="reason" maxlength="1000" required></textarea>
                    <label for="housing">Where do you live?</label>
                    <select id="housing" name="housing" required>
                        <option value="">Choose…</option>
                        <c:forEach items="${housingTypes}" var="h"><option value="${h}"><c:out value="${h}"/></option></c:forEach>
                    </select>
                    <label for="experience">Experience with pets (optional)</label>
                    <input type="text" id="experience" name="experience" maxlength="255" placeholder="e.g. Grew up with dogs for 10 years">
                    <label class="check" style="margin-top:1rem"><input type="checkbox" name="hasOtherPets" value="yes"> I already have other pets at home</label>
                    <div class="form-actions"><button class="btn" type="submit">Submit application</button></div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
