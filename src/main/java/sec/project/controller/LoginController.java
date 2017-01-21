package sec.project.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sec.project.domain.Account;
import sec.project.repository.AccountRepository;

@Controller
public class LoginController {

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loadForm(HttpServletResponse response) {
        return "login";
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logout(HttpServletResponse response) {
        Cookie resetSessionTokenCookie = new Cookie("sessiontoken", null);
        Cookie resetSessionIdCookie = new Cookie("sessionid", null);
        resetSessionTokenCookie.setMaxAge(0); // Clear cookies
        resetSessionIdCookie.setMaxAge(0); // Clear cookies
        
        response.addCookie(resetSessionTokenCookie);
        response.addCookie(resetSessionIdCookie);
        
        return "redirect:/login";
    }
    
    @RequestMapping(value = "/forgotpassword", method = RequestMethod.GET)
    public String forgotPassword(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            Account a = accountRepository.findOne(id);
            if (a != null) {
                model.addAttribute("message", 
                        "Your password has been sent to " + a.getEmail());
            }
        }
        
        return "forgotpassword";
    }
    
    @RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
    public String forgotPassword(@RequestParam String email, RedirectAttributes ra) {
        Account existingAccount = accountRepository.findByEmail(email);
        if (existingAccount != null) {
            ra.addAttribute("id", existingAccount.getId());
        }
        
        return "redirect:/forgotpassword";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String submitForm(Model model, HttpServletResponse response,
            @RequestParam String email, @RequestParam String password) {
        Account existingAccount = accountRepository.findByEmail(email);
        if (existingAccount == null) {
            model.addAttribute("error", "No such account");
                return "login";
        } else {
            if (!existingAccount.getPassword().equals(password)) {                
                model.addAttribute("error", "Invalid password");
                return "login";
            }
        }
        
        response.addCookie(new Cookie("sessionid", existingAccount.getId().toString()));
        response.addCookie(new Cookie("sessiontoken", UltraSecure.getSessionToken(password)));
        
        return "redirect:/";
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(Model model,
            @RequestParam String email, @RequestParam String name, @RequestParam String password) {
        Account existingAccount = accountRepository.findByEmail(email);
        if (existingAccount != null) {
            model.addAttribute("error", "Account already exists");
        } else {            
            accountRepository.save(new Account(email, name, password));
            model.addAttribute("message", "You can now log in");
        }
        
        return "login";
    }
}
