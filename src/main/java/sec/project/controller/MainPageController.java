package sec.project.controller;

import java.util.ArrayList;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Account;
import sec.project.domain.Post;
import sec.project.repository.AccountRepository;
import sec.project.repository.PostRepository;

@Controller
public class MainPageController {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private PostRepository postRepository;

    @RequestMapping("*")
    public String defaultMapping(HttpServletRequest request, Model model) {                        
        if (accountRepository.count() == 0) {
            addUsers();
        }
        
        if (!validUserLoggedIn(request)) {
            return "redirect:/login";            
        }
        
        model.addAttribute("userName", getLoggedInUser(request).getName());
        model.addAttribute("posts", postRepository.findAll());
        
        return "mainpage";
    }
    
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    private String postPost(HttpServletRequest request, String message) {                        
        postRepository.save(new Post(getLoggedInUser(request), message));
        
        return "redirect:/";
    }
    
    private Account getLoggedInUser(HttpServletRequest request) {      
        String userId = "";
        for (Cookie c : request.getCookies()) {            
            if (c.getName().equals("sessionid")) {
                userId = c.getValue();
            }
        }
        Account acc = accountRepository.findOne(new Long(userId));
                
        return acc;
    }
    
    private boolean validUserLoggedIn(HttpServletRequest request) {      
        String tokenToCheck = "";
        String idToCheck = "";
        
        if (request == null || request.getCookies() == null) {
            return false;
        }
        
        for (Cookie c : request.getCookies()) {
            String cookieName = c.getName();
            if (cookieName.equals("sessionid")) {
                idToCheck = c.getValue();
            } else if (cookieName.equals("sessiontoken")) {
                tokenToCheck = c.getValue();
            }
        }
        
        if (tokenToCheck.isEmpty() || idToCheck.isEmpty()) {
            return false;
        }
        
        Account acc = accountRepository.findOne(new Long(idToCheck));
        if (acc == null) {
            return false;
        }        
        
        return UltraSecure.tokenBelongsToAccount(tokenToCheck, acc);
    }

    private void addUsers() {
        ArrayList<String> firstNames = new ArrayList();                
        firstNames.add("Marja");
        firstNames.add("Seppo");
        firstNames.add("Nikita");
        firstNames.add("SpeedDemon");
        firstNames.add("Sammy");
        firstNames.add("Jooseppi");
        firstNames.add("David");
        firstNames.add("Zuck");
        ArrayList<String> lastNames = new ArrayList();         
        lastNames.add("Virtanen");
        lastNames.add("666");
        lastNames.add("Vsailitzev"); 
        lastNames.add("Marjanen"); 
        lastNames.add("Kilminster");
        lastNames.add("Partanen");
        lastNames.add("Waenaemoenen");
        lastNames.add("Routainen");
        lastNames.add("Smith");
        lastNames.add("Davidson");
        lastNames.add("Marckerberg");
        ArrayList<String> messages = new ArrayList();
        messages.add("Kuontalo-opus on paras!");
        messages.add("Söin juuri soppaa");
        messages.add("Souperino is besteroni");
        messages.add("Call 555-2156 for free Star Wars tickets!");
        messages.add("kuukle vw jakohihna perähikiä");
        messages.add("KO on jeejee");
        messages.add("ヽ༼ຈل͜ຈ༽ﾉ woo");
        messages.add("Kuka tietää miten tänne lisätään postaus");               

        // Test account
        Account test = new Account("test@test.com", "Test Man", "test");
        accountRepository.save(test);
        
        for (int i = 0; i < 5; i++) {
            String fName = firstNames.remove((int)(Math.random() * firstNames.size()));
            String lName = lastNames.remove((int)(Math.random() * lastNames.size()));
            String msg = messages.remove((int)(Math.random() * messages.size())); 
            
            Account a = new Account(i + "@keemaili.fi", fName + " "+ lName, lName + "" + i);
            accountRepository.save(a);
            
            Post p = new Post(a, msg);
            postRepository.save(p);            
        }                
    }
}
