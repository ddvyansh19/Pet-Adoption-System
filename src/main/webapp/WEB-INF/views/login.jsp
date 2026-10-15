<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="card auth-card">
    <h1>Welcome back 🐶</h1>
    <p class="muted">Log in to browse pets, manage listings or administer the platform.</p>
    <form method="post" action="${ctx}/login">
        <t:csrf/>
        <label for="email">Email</label>
        <input type="email" id="email" name="email" value="<c:out value='${param.email}'/>" required autofocus>
        <label for="password">Password</label>
        <input type="password" id="password" name="password" required>
        <div class="form-actions">
            <button class="btn btn-block" type="submit">Log in</button>
        </div>
    </form>
    <p class="muted small" style="margin-top:1rem">New here? <a href="${ctx}/register">Create an account</a></p>
    <div class="demo">
        <strong>Demo accounts</strong><br>
        Admin: <code>admin@petadopt.com</code> / <code>Admin@123</code><br>
        Shelter: <code>shelter@petadopt.com</code> / <code>Shelter@123</code><br>
        Adopter: <code>adopter@petadopt.com</code> / <code>Adopter@123</code>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
