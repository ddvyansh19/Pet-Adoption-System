<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1><c:out value="${editing ? 'Edit pet' : 'List a new pet'}"/></h1>
        <p class="muted">Clear photos and an honest description help pets find homes faster.</p>
    </div>
    <a class="btn btn-secondary" href="${ctx}/shelter/pets">← Back to my pets</a>
</div>

<div class="card" style="max-width:720px">
    <form method="post" action="${ctx}/shelter/pets" enctype="multipart/form-data">
        <t:csrf/>
        <input type="hidden" name="action" value="${editing ? 'update' : 'create'}">
        <c:if test="${editing}"><input type="hidden" name="id" value="${formPet.id}"></c:if>

        <div class="grid grid-2" style="gap:0 1.25rem">
            <div>
                <label for="name">Pet name</label>
                <input type="text" id="name" name="name" maxlength="100" value="<c:out value='${formPet.name}'/>" required>
            </div>
            <div>
                <label for="type">Type</label>
                <select id="type" name="type" required>
                    <option value="">Choose…</option>
                    <c:forEach items="${petTypes}" var="pt">
                        <option value="${pt}" ${formPet.type == pt ? 'selected' : ''}><c:out value="${pt}"/></option>
                    </c:forEach>
                </select>
            </div>
            <div>
                <label for="breed">Breed</label>
                <input type="text" id="breed" name="breed" maxlength="100" value="<c:out value='${formPet.breed}'/>" required>
            </div>
            <div>
                <label for="age">Age (years, e.g. 0.5 for 6 months)</label>
                <input type="number" id="age" name="age" min="0" max="40" step="0.1"
                       value="<c:if test='${editing or not empty formPet.name}'><c:out value='${formPet.ageYears}'/></c:if>" required>
            </div>
            <div>
                <label for="location">Location (city)</label>
                <input type="text" id="location" name="location" maxlength="120" value="<c:out value='${formPet.location}'/>" required>
            </div>
            <c:if test="${editing}">
                <div>
                    <label for="adoptionStatus">Adoption status</label>
                    <select id="adoptionStatus" name="adoptionStatus">
                        <option value="AVAILABLE" ${formPet.available ? 'selected' : ''}>Available</option>
                        <option value="ADOPTED" ${formPet.adopted ? 'selected' : ''}>Adopted</option>
                    </select>
                </div>
            </c:if>
        </div>

        <label for="description">Description</label>
        <textarea id="description" name="description" maxlength="2000"><c:out value="${formPet.description}"/></textarea>

        <label for="photo">Photo (JPG, PNG, GIF or WebP, max 5 MB)</label>
        <c:if test="${editing and not empty formPet.photoPath}">
            <div style="margin-bottom:.5rem"><t:thumb photo="${formPet.photoPath}" type="${formPet.type}" size="md"/>
                <span class="muted small">Current photo. Choose a file below to replace it.</span></div>
        </c:if>
        <input type="file" id="photo" name="photo" accept="image/jpeg,image/png,image/gif,image/webp">

        <div class="form-actions">
            <button class="btn" type="submit"><c:out value="${editing ? 'Save changes' : 'Submit listing'}"/></button>
            <a class="btn btn-secondary" href="${ctx}/shelter/pets">Cancel</a>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
