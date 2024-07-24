<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Logout Page</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<form action = "LogoutServlet">
    <div class="logout-container">
        <h1>You have been logged out</h1>
        <p>Thank you for using our service.</p>
        <a href="login.jsp" class="login-link">Login Again</a>
    </div>
    </form>
</body>
</html>
