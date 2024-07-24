<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="bank.controllers.RegisterCustomerServlet" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Registration Success</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .container {
            background-color: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        h1 {
            color: #333;
        }
        p {
            color: #666;
        }
        .success {
            color: #28a745;
            font-weight: bold;
        }
        .info {
            margin: 10px 0;
        }
        .info span {
            font-weight: bold;
        }
        .footer {
            margin-top: 20px;
            color: #aaa;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Registration Success</h1>
        <p class="success">Your account has been successfully created!</p>
        <div class="info">
            <% 
                String accountno = RegisterCustomerServlet.generateAccountNumber();
                String password = RegisterCustomerServlet.generatePassword();
            %>
            <p><span>Account Number:</span> <%= accountno %></p>
            <p><span>Temporary Password:</span> <%= password %></p>
        </div>
        <p>Please change your temporary password after your first login for security reasons.</p>
        <div class="footer">
            <p>Thank you for choosing our bank!</p>
        </div>
    </div>
</body>
</html>
