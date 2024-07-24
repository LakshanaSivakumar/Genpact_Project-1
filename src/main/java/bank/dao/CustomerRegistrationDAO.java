package bank.dao;

import java.sql.*;
import java.util.Random;
import bank.model.Customer;
import util.DataBaseUtil;

public class CustomerRegistrationDAO {
    
    public long registerCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (Fullname, Address, Phoneno, Emailid, Acctype, Balance, DOB, Idproof, Username, Accno, Temppassword) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        
        long Accno = generateAccountNumber();
        String Temppassword = generateTemporaryPassword();
        
        try (Connection conn = DataBaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getFullname());
            pstmt.setString(2, customer.getAddress());
            pstmt.setLong(3, customer.getPhoneno());
            pstmt.setString(4, customer.getEmailid());
            pstmt.setDate(5, Date.valueOf(customer.getDOB()));
            pstmt.setString(6, customer.getAcctype());
            pstmt.setFloat(7, customer.getBalance());
            pstmt.setString(8, customer.getIdproof());
            pstmt.setString(9, customer.getUsername());
            pstmt.setLong(10, Accno);
            pstmt.setString(11, Temppassword);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }
        }
        
        return Accno;
    }
    
    private long generateAccountNumber() {
        // This is a simple implementation. In a real-world scenario, 
        // you might want to ensure uniqueness and use a more sophisticated method.
        return 1000000000L + new Random().nextInt(900000000);
    }
    
    private String generateTemporaryPassword() {
        // Generate a random 8-character password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}