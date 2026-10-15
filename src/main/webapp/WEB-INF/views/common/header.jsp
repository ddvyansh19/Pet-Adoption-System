<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="sp" value="${requestScope['javax.servlet.forward.servlet_path']}"/>
<c:set var="role" value="${sessionScope.user.roleName}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${pageTitle}"/> | <c:out value="${empty siteName ? 'Paws & Homes' : siteName}"/></title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<header class="topbar">
    <div class="container topbar-inner">
        <a class="brand" href="${ctx}/">🐾 <c:out value="${empty siteName ? 'Paws & Homes' : siteName}"/></a>
        <c:if test="${not empty sessionScope.user}">
            <nav class="nav">
                <c:choose>
                    <c:when test="${role == 'ADMIN'}">
                        <a class="${sp == '/admin/dashboard' ? 'active' : ''}" href="${ctx}/admin/dashboard">Dashboard</a>
                        <a class="${sp == '/admin/users' ? 'active' : ''}" href="${ctx}/admin/users">Users</a>
                        <a class="${sp == '/admin/pets' ? 'active' : ''}" href="${ctx}/admin/pets">Pet listings</a>
                        <a class="${sp == '/admin/settings' ? 'active' : ''}" href="${ctx}/admin/settings">Settings</a>
                    </c:when>
                    <c:when test="${role == 'SHELTER'}">
                        <a class="${sp == '/shelter/dashboard' ? 'active' : ''}" href="${ctx}/shelter/dashboard">Dashboard</a>
                        <a class="${sp == '/shelter/pets' ? 'active' : ''}" href="${ctx}/shelter/pets">My pets</a>
                        <a class="${sp == '/shelter/applications' ? 'active' : ''}" href="${ctx}/shelter/applications">Applications</a>
                        <a class="${sp == '/shelter/messages' ? 'active' : ''}" href="${ctx}/shelter/messages">Messages<c:if test="${unreadCount > 0}"><span class="count">${unreadCount}</span></c:if></a>
                    </c:when>
                    <c:otherwise>
                        <a class="${sp == '/adopter/dashboard' ? 'active' : ''}" href="${ctx}/adopter/dashboard">Dashboard</a>
                        <a class="${sp == '/adopter/browse' || sp == '/adopter/pet' ? 'active' : ''}" href="${ctx}/adopter/browse">Browse pets</a>
                        <a class="${sp == '/adopter/applications' ? 'active' : ''}" href="${ctx}/adopter/applications">Applications</a>
                        <a class="${sp == '/adopter/history' ? 'active' : ''}" href="${ctx}/adopter/history">History</a>
                        <a class="${sp == '/adopter/messages' ? 'active' : ''}" href="${ctx}/adopter/messages">Messages<c:if test="${unreadCount > 0}"><span class="count">${unreadCount}</span></c:if></a>
                        <a class="${sp == '/adopter/profile' ? 'active' : ''}" href="${ctx}/adopter/profile">Profile</a>
                    </c:otherwise>
                </c:choose>
            </nav>
            <div class="user-chip">
                <span><c:out value="${sessionScope.user.name}"/></span>
                <span class="badge badge-${role}"><c:out value="${sessionScope.user.roleLabel}"/></span>
                <form class="inline" method="post" action="${ctx}/logout">
                    <input type="hidden" name="csrf" value="${sessionScope.csrfToken}">
                    <button type="submit" class="btn btn-link">Log out</button>
                </form>
            </div>
        </c:if>
    </div>
</header>
<main class="container">
    <c:if test="${not empty sessionScope.flashSuccess}">
        <div class="alert alert-success"><c:out value="${sessionScope.flashSuccess}"/></div>
        <c:remove var="flashSuccess" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.flashError}">
        <div class="alert alert-error"><c:out value="${sessionScope.flashError}"/></div>
        <c:remove var="flashError" scope="session"/>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>
    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul>
                <c:forEach items="${errors}" var="msg"><li><c:out value="${msg}"/></li></c:forEach>
            </ul>
        </div>
    </c:if>
