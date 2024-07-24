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
import util.AccountUtils;

@WebServlet("/TryServlet")
public class TryServlet extends HttpServlet {

    private static final long serialVersionUID = 1796381764981520343L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/dbforbankapp?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "sqllogin";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve form parameters
        String Fullname = request.getParameter("Fullname");
        String phonenumber = request.getParameter("phonenumber");
        String emailid = request.getParameter("emailid");
        String address = request.getParameter("address");
        String dob = request.getParameter("DOB");
        String typeofaccount = request.getParameter("accounttype");
        String idproof = request.getParameter("idproof");
        String username = request.getParameter("username");
        String initialbalanceStr = request.getParameter("initialbalance");

        // Convert and validate date of birth
        LocalDate dobDate = null;
        try {
            dobDate = LocalDate.parse(dob);
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
        String accountno = AccountUtils.generateAccountNumber();
        String password = AccountUtils.generatePassword();

        // Convert initial balance
        double initialbalance;
        try {
            initialbalance = Double.parseDouble(initialbalanceStr);
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
            checkStmt.setString(1, username);
            rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                out.write("Error: Username already exists. Please choose a different username.");
                return;
            }

            // Insert new record
            String sql = "INSERT INTO customer (Fullname, Address, Phoneno, Emailid, DOB, Accno, Acctype, Idproof, Username, Balance, Temppassword) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, Fullname);
            stmt.setString(2, address);
            stmt.setString(3, phonenumber);
            stmt.setString(4, emailid);
            stmt.setDate(5, java.sql.Date.valueOf(dobDate));
            stmt.setString(6, accountno);
            stmt.setString(7, typeofaccount);
            stmt.setString(8, idproof);
            stmt.setString(9, username);
            stmt.setDouble(10, initialbalance);
            stmt.setString(11, password);
            stmt.executeUpdate();

            // Forward to JSP page with success message
            request.setAttribute("accountno", accountno);
            request.setAttribute("password", password);
            request.getRequestDispatcher("/regsuccess.jsp").forward(request, response);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            out.write("Error: MySQL JDBC driver not found. Please check the driver configuration.");
        } catch (SQLException e) {
            e.printStackTrace();
            out.write("Error: Database error occurred. Details: " + e.getMessage());
        }catch(NullPointerException e) {
        	e.printStackTrace();
            out.write("Error: Null String error occurred. Details: " + e.getMessage());	
        }
        finally {
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

}