package COMP390.PlanMe.Controllers.Login;

import COMP390.PlanMe.Dao.UserDAO;
import COMP390.PlanMe.Entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostLoginController {
    private UserDAO userDAO;
    @Autowired
    public PostLoginController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    @PostMapping("/login")
    private String processLoginForm(@RequestParam("email") String email,@RequestParam("password") String password,
                                    Model model, HttpSession session) {
        User user = userDAO.findByEmail(email);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (user!= null) {
            if(passwordEncoder.matches(password, user.getPassword())) { // check users password when logging in
                session.setAttribute("user", user);
                return "redirect:/homepage";
            } else {
                model.addAttribute("error", "Incorrect password");
                return "Login";
            }
        } else {
            model.addAttribute("error", "Incorrect email");
            return "Login";
        }
    }
    @PostMapping("/logout")
    private String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

//Model is used for displaying error message on login html page

//Session is used for storing logged-in user's information