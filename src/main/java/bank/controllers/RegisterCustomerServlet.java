package bank.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegisterCustomerServlet")
public class RegisterCustomerServlet extends HttpServlet {

    private static final long serialVersionUID = 1796381764981520343L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/dbforbankapp?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "sqllogin";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve form parameters
        String Fullname = request.getParameter("Fullname");
        String Address = request.getParameter("Address");
        String Phoneno = request.getParameter("mobileNo");
        String Emailid = request.getParameter("emailid");
        String Acctype = request.getParameter("accountType");
        String Balance = request.getParameter("Balance");
        String DOB = request.getParameter("DOB");
        String Idproof = request.getParameter("idproof");
        String Accno = request.getParameter("Accno");
        String Temppassword = request.getParameter("Temppassword");
        String Username = request.getParameter("username");

        // Convert and validate date of birth
        LocalDate dobDate = null;
        try {
            dobDate = LocalDate.parse(DOB);
            int age = Period.between(dobDate, LocalDate.now()).getYears();
            if (age < 18) {
                out.write("Error: Age must be 18 or older.");
                return;
            }
        } catch (DateTimeParseException e) {
            out.write("Error: Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        // Generate account number and password
        String accountno = generateAccountNumber();
        String password = generatePassword();

        // Convert initial balance
        double initialbalance;
        try {
            initialbalance = Double.parseDouble(Balance);
        } catch (NumberFormatException e) {
            out.write("Error: Invalid initial balance. Please enter a numeric value.");
            return;
        }

        // Database connection and insertion
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement checkStmt = null;
        ResultSet rs = null;

        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // Check if the username already exists
            String checkSql = "SELECT COUNT(*) FROM customer WHERE Username = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, Username);
            rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                out.write("Error: Username already exists. Please choose a different username.");
                return;
            }

            // Insert new record
            String sql = "INSERT INTO customer (Fullname, Address, Phoneno, Emailid,Acctype,Balance, DOB, Idproof,Username, Accno, Temppassword) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, Fullname);
            stmt.setString(2, Address);
            stmt.setString(3, Phoneno);
            stmt.setString(4, Emailid);
            stmt.setString(5, Acctype);
            stmt.setString(6, Balance);
            stmt.setDate(7, java.sql.Date.valueOf(dobDate));
            stmt.setString(8, Idproof);
            stmt.setString(9, Username);
            stmt.setString(10, Accno);
            stmt.setString(11, Temppassword);
            stmt.executeUpdate();

            // Forward to JSP page with success message
            request.setAttribute("Accno", Accno);
            request.setAttribute("Temppassword", Temppassword);
            request.getRequestDispatcher("/regsuccess.jsp").forward(request, response);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            out.write("Error: MySQL JDBC driver not found. Please check the driver configuration.");
        } catch (SQLException e) {
            e.printStackTrace();
            out.write("Error: Database error occurred. Details: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static String generateAccountNumber() {
        // Generate a unique account number
        return String.format("%010d", (int)(Math.random() * 1_000_000_000));
    }

    public static String generatePassword() {
        // Generate a shorter, unique password
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        int passwordLength = 8; // Desired password length

        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            password.append(characters.charAt(randomIndex));
        }

        return password.toString();
    }
}