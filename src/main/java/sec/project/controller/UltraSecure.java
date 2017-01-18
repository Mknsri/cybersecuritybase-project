package sec.project.controller;

import sec.project.domain.Account;

public class UltraSecure {
    
    // Awesome function 
    public static String getSessionToken(String password) {
        String token = "";
        for (char c : password.toCharArray()) {
            token += Character.getNumericValue(c);
        }
        
        return token;
    }
    
    public static boolean tokenBelongsToAccount(String token, Account acc) {
        return token.equals(getSessionToken(acc.getPassword()));
    }
}
