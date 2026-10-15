<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/common/header.jsp"/>
<c:set var="isShelter" value="${sessionScope.user.roleName == 'SHELTER'}"/>

<div class="page-head">
    <div>
        <h1>Messages</h1>
        <p class="muted">
            <c:choose>
                <c:when test="${isShelter}">Talk to adopters who applied for your pets.</c:when>
                <c:otherwise>Talk to the shelters of the pets you applied for.</c:otherwise>
            </c:choose>
        </p>
    </div>
</div>

<div class="grid grid-sidebar">
    <div class="card">
        <h2>New message</h2>
        <c:choose>
            <c:when test="${empty contacts}">
                <p class="muted">
                    <c:choose>
                        <c:when test="${isShelter}">You can message an adopter once they apply for one of your pets.</c:when>
                        <c:otherwise>You can message a shelter once you have applied for one of its pets.</c:otherwise>
                    </c:choose>
                </p>
            </c:when>
            <c:otherwise>
                <form method="post" action="${ctx}${basePath}">
                    <t:csrf/>
                    <label for="receiverId" style="margin-top:0">To</label>
                    <select id="receiverId" name="receiverId" required>
                        <c:forEach items="${contacts}" var="contact">
                            <option value="${contact.id}"><c:out value="${contact.name}"/></option>
                        </c:forEach>
                    </select>
                    <label for="content">Message</label>
                    <textarea id="content" name="content" maxlength="2000" required></textarea>
                    <div class="form-actions"><button class="btn" type="submit">Send message</button></div>
                </form>
            </c:otherwise>
        </c:choose>
    </div>

    <div>
        <div class="tabs">
            <a class="${box == 'inbox' ? 'active' : ''}" href="${ctx}${basePath}">Inbox</a>
            <a class="${box == 'sent' ? 'active' : ''}" href="${ctx}${basePath}?box=sent">Sent</a>
        </div>
        <c:choose>
            <c:when test="${empty messages}">
                <div class="card empty"><div class="big">💬</div>No messages here yet.</div>
            </c:when>
            <c:otherwise>
                <c:forEach items="${messages}" var="m">
                    <div class="msg ${box == 'inbox' and not m.read ? 'unread' : ''}">
                        <div class="meta">
                            <c:choose>
                                <c:when test="${box == 'inbox'}">From <strong><c:out value="${m.senderName}"/></strong></c:when>
                                <c:otherwise>To <strong><c:out value="${m.receiverName}"/></strong></c:otherwise>
                            </c:choose>
                            · <fmt:formatDate value="${m.sentAt}" pattern="dd MMM yyyy, HH:mm"/>
                        </div>
                        <p><c:out value="${m.content}"/></p>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
