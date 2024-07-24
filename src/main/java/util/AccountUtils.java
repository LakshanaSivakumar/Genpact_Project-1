package util;
import java.security.SecureRandom;

public class AccountUtils {

	    public static String generateAccountNumber() {
	        // Generate a unique account number
	        return String.format("%010d", (int)(Math.random() * 1_000_000_000));
	    }

	    public static String generatePassword() {
	        // Generate a shorter, unique password
	        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	        StringBuilder password = new StringBuilder();
	        int passwordLength = 8; // Desired password length

	        SecureRandom random = new SecureRandom();
	        for (int i = 0; i < passwordLength; i++) {
	            int randomIndex = random.nextInt(characters.length());
	            password.append(characters.charAt(randomIndex));
	        }

	        return password.toString();
	    }
	}

}
