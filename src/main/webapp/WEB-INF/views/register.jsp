<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card auth-card">
    <h1>Create your account</h1>
    <p class="muted">Adopters can apply for pets. Shelters can list pets and review applications.</p>
    <form method="post" action="${ctx}/register">
        <t:csrf/>
        <label for="role">I am a…</label>
        <select id="role" name="role" required>
            <option value="ADOPTER" ${param.role != 'SHELTER' ? 'selected' : ''}>Adopter (looking for a pet)</option>
            <option value="SHELTER" ${param.role == 'SHELTER' ? 'selected' : ''}>Shelter / rescue (listing pets)</option>
        </select>
        <label for="name">Full name / organisation</label>
        <input type="text" id="name" name="name" maxlength="100" value="<c:out value='${param.name}'/>" required>
        <label for="email">Email</label>
        <input type="email" id="email" name="email" maxlength="150" value="<c:out value='${param.email}'/>" required>
        <label for="phone">Phone (optional)</label>
        <input type="tel" id="phone" name="phone" maxlength="20" value="<c:out value='${param.phone}'/>">
        <label for="address">Address (optional)</label>
        <input type="text" id="address" name="address" maxlength="255" value="<c:out value='${param.address}'/>">
        <label for="password">Password</label>
        <input type="password" id="password" name="password" minlength="8" required>
        <p class="muted small">At least 8 characters, with letters and digits.</p>
        <label for="confirm">Confirm password</label>
        <input type="password" id="confirm" name="confirm" minlength="8" required>
        <div class="form-actions">
            <button class="btn btn-block" type="submit">Sign up</button>
        </div>
    </form>
    <p class="muted small" style="margin-top:1rem">Already registered? <a href="${ctx}/login">Log in</a></p>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
