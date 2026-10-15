<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>403 | Paws &amp; Homes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main class="container">
    <div class="card auth-card empty">
        <div class="big">🐾</div>
        <h1>403 &middot; Access denied</h1>
        <p>You do not have permission to open this page, or your security token expired. Go back, reload the page and try again.</p>
        <a class="btn" href="${pageContext.request.contextPath}/">Back to home</a>
    </div>
</main>
</body>
</html>
