<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="page-head">
    <div>
        <h1>My profile</h1>
        <p class="muted">Keep your contact details up to date so shelters can reach you.</p>
    </div>
</div>

<div class="grid grid-2">
    <div class="card">
        <h2>Contact details</h2>
        <form method="post" action="${ctx}/adopter/profile">
            <t:csrf/>
            <input type="hidden" name="action" value="profile">
            <label for="name" style="margin-top:0">Full name</label>
            <input type="text" id="name" name="name" maxlength="100"
                   value="<c:out value='${empty param.name ? sessionScope.user.name : param.name}'/>" required>
            <label for="email">Email (cannot be changed)</label>
            <input type="email" id="email" value="<c:out value='${sessionScope.user.email}'/>" disabled>
            <label for="phone">Phone</label>
            <input type="tel" id="phone" name="phone" maxlength="20"
                   value="<c:out value='${param.action == "profile" ? param.phone : sessionScope.user.phone}'/>">
            <label for="address">Address</label>
            <input type="text" id="address" name="address" maxlength="255"
                   value="<c:out value='${param.action == "profile" ? param.address : sessionScope.user.address}'/>">
            <div class="form-actions"><button class="btn" type="submit">Save profile</button></div>
        </form>
    </div>

    <div class="card">
        <h2>Change password</h2>
        <form method="post" action="${ctx}/adopter/profile">
            <t:csrf/>
            <input type="hidden" name="action" value="password">
            <label for="current" style="margin-top:0">Current password</label>
            <input type="password" id="current" name="current" required>
            <label for="next">New password</label>
            <input type="password" id="next" name="next" minlength="8" required>
            <label for="confirm">Confirm new password</label>
            <input type="password" id="confirm" name="confirm" minlength="8" required>
            <div class="form-actions"><button class="btn" type="submit">Change password</button></div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
