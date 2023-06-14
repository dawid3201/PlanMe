package COMP390.PlanMe;

import COMP390.PlanMe.dao.UserDAO;
import COMP390.PlanMe.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private UserDAO userDAO;

    public LoginController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    @PostMapping("/login")
    private String processLoginForm(@RequestParam("email") String email, Model model, HttpSession session) {
        boolean emailExists = userDAO.checkEmailExists(email);
        if (emailExists) {
            // Perform login logic
            User user = userDAO.getUserByEmail(email);
            session.setAttribute("user", user);
            return "redirect:/dashboard"; // Redirect to the dashboard after successful login
        } else {
            model.addAttribute("error", "Incorrect email"); // Add error message to the model
            return "login"; // Return to the login form with error message
        }
    }
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        } else {
            // Redirect to login page if no user is logged in
            return "redirect:/login";
        }
        return "dashboard";
    }

    @PostMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }



    //TODO: check if password matches
}
