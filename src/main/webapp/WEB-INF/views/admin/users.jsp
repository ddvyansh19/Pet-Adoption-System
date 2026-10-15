<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<c:set var="isEdit" value="${not empty formUser and formUser.id > 0}"/>

<div class="page-head">
    <div>
        <h1>User management</h1>
        <p class="muted">Create accounts, change roles and remove users.</p>
    </div>
</div>

<div class="grid grid-sidebar">
    <%-- create / edit form --%>
    <div class="card">
        <h2><c:out value="${isEdit ? 'Edit user' : 'Add a user'}"/></h2>
        <form method="post" action="${ctx}/admin/users">
            <t:csrf/>
            <input type="hidden" name="action" value="${isEdit ? 'update' : 'create'}">
            <c:if test="${isEdit}"><input type="hidden" name="id" value="${formUser.id}"></c:if>

            <label for="name">Name</label>
            <input type="text" id="name" name="name" maxlength="100" value="<c:out value='${formUser.name}'/>" required>
            <label for="email">Email</label>
            <input type="email" id="email" name="email" maxlength="150" value="<c:out value='${formUser.email}'/>" required>
            <label for="role">Role</label>
            <select id="role" name="role">
                <c:forEach items="${roles}" var="r">
                    <option value="${r}" ${(not empty formUser and formUser.role == r) or (empty formUser and r.name() == 'ADOPTER') ? 'selected' : ''}><c:out value="${r.label}"/></option>
                </c:forEach>
            </select>
            <label for="phone">Phone</label>
            <input type="tel" id="phone" name="phone" maxlength="20" value="<c:out value='${formUser.phone}'/>">
            <label for="address">Address</label>
            <input type="text" id="address" name="address" maxlength="255" value="<c:out value='${formUser.address}'/>">
            <label for="password"><c:out value="${isEdit ? 'New password (leave blank to keep)' : 'Password'}"/></label>
            <input type="password" id="password" name="password" minlength="8" ${isEdit ? '' : 'required'}>

            <div class="form-actions">
                <button class="btn" type="submit"><c:out value="${isEdit ? 'Save changes' : 'Create user'}"/></button>
                <c:if test="${isEdit}"><a class="btn btn-secondary" href="${ctx}/admin/users">Cancel</a></c:if>
            </div>
        </form>
    </div>

    <%-- user table --%>
    <div class="card">
        <h2>All users (${users.size()})</h2>
        <form class="filter-bar" method="get" action="${ctx}/admin/users" style="margin-bottom:1rem">
            <div>
                <label for="role_filter">Filter by role</label>
                <select id="role_filter" name="role_filter" onchange="this.form.submit()">
                    <option value="">All roles</option>
                    <c:forEach items="${roles}" var="r">
                        <option value="${r}" ${roleFilter == r.name() ? 'selected' : ''}><c:out value="${r.label}"/></option>
                    </c:forEach>
                </select>
            </div>
        </form>
        <div class="table-wrap">
        <table>
            <thead><tr><th>Name</th><th>Role</th><th>Phone</th><th>Joined</th><th></th></tr></thead>
            <tbody>
            <c:forEach items="${users}" var="u">
                <tr>
                    <td><strong><c:out value="${u.name}"/></strong><br><span class="muted small"><c:out value="${u.email}"/></span></td>
                    <td><t:status value="${u.role}"/></td>
                    <td><c:out value="${empty u.phone ? '—' : u.phone}"/></td>
                    <td><fmt:formatDate value="${u.createdAt}" pattern="dd MMM yyyy"/></td>
                    <td class="right">
                        <a class="btn btn-sm btn-secondary" href="${ctx}/admin/users?edit=${u.id}">Edit</a>
                        <c:if test="${u.id != sessionScope.user.id}">
                            <form class="inline" method="post" action="${ctx}/admin/users"
                                  onsubmit="return confirm('Delete this user and everything linked to them (pets, applications, messages)?');">
                                <t:csrf/>
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${u.id}">
                                <button class="btn btn-sm btn-danger" type="submit">Delete</button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
