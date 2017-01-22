package sec.project.controller;

import sec.project.config.SecurityConfiguration;
import sec.project.domain.Account;

public class UltraSecure {
    
    // Awesome function 
    public static String getSessionToken(String password) {                
        String token = 
                new SecurityConfiguration().passwordEncoder().encode(password);
        
        return token;
    }
    
    public static boolean tokenBelongsToAccount(String token, Account acc) {
        return new SecurityConfiguration()
                .passwordEncoder()
                .matches(acc.getPassword(), token);
    }
}
