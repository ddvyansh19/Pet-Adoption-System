<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ attribute name="pet" required="true" type="com.petadopt.model.Pet" %>
<a class="pet-card" href="${ctx}/adopter/pet?id=${pet.id}">
    <t:thumb photo="${pet.photoPath}" type="${pet.type}" size="lg"/>
    <div class="pet-card-body">
        <h3><c:out value="${pet.name}"/></h3>
        <p class="muted"><c:out value="${pet.breed}"/> · <c:out value="${pet.ageLabel}"/></p>
        <p class="muted small">📍 <c:out value="${pet.location}"/> · <c:out value="${pet.shelterName}"/></p>
    </div>
</a>
