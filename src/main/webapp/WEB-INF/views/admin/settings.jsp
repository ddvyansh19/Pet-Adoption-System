<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1>System settings</h1>
        <p class="muted">These settings apply to the whole platform and take effect immediately.</p>
    </div>
</div>

<div class="card" style="max-width:620px">
    <form method="post" action="${ctx}/admin/settings">
        <t:csrf/>
        <label for="site_name">Site name</label>
        <input type="text" id="site_name" name="site_name" maxlength="60" value="<c:out value='${formSettings.site_name}'/>" required>

        <label for="contact_email">Support contact email</label>
        <input type="email" id="contact_email" name="contact_email" maxlength="150" value="<c:out value='${formSettings.contact_email}'/>" required>

        <label for="max_active_applications">Max pending applications per adopter</label>
        <input type="number" id="max_active_applications" name="max_active_applications" min="1" max="20"
               value="<c:out value='${formSettings.max_active_applications}'/>" required>

        <label for="allow_registration">New registrations</label>
        <select id="allow_registration" name="allow_registration">
            <option value="true" ${formSettings.allow_registration == 'true' ? 'selected' : ''}>Open: anyone can sign up</option>
            <option value="false" ${formSettings.allow_registration != 'true' ? 'selected' : ''}>Closed: only admins can create users</option>
        </select>

        <div class="form-actions"><button class="btn" type="submit">Save settings</button></div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
