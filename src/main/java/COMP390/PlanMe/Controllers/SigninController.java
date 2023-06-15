package COMP390.PlanMe.Controllers;

import COMP390.PlanMe.dao.HomepageDAO;
import COMP390.PlanMe.dao.UserDAO;
import COMP390.PlanMe.entity.Homepage;
import COMP390.PlanMe.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class SigninController {
    private UserDAO userDAO;

    @Autowired
    private HomepageDAO homepageDAO;

    public SigninController(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    @GetMapping("/signin")
    public String showSigninForm(){
        return "signin";
    }

    @PostMapping("/signin")
    public String processSigninForm(@Validated @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "signin";
        }

        if (!isValidEmail(user.getEmail())) {
            model.addAttribute("emailError", "Invalid email format");
            return "signin";
        }

        if (!isValidPassword(user.getPassword())) {
            model.addAttribute("passwordError", "Password must be between 10 and 12 characters");
            return "signin";
        }

        boolean emailExists = userDAO.checkEmailExists(user.getEmail());
        if (emailExists) {
            model.addAttribute("emailError", "Email already exists");
            return "signin";
        }

        userDAO.save(user);

        Homepage homepage = new Homepage();
        homepage.setUser(user);
        homepageDAO.save(homepage);
        return "redirect:/homepage";
    }
    private boolean isValidEmail(String email) {
        // Implement your email validation logic here
        // Example: using regex pattern matching
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        // Implement your password length validation logic here
        int passwordLength = password.length();
        return passwordLength >= 10 && passwordLength <= 12;
    }

    //TODO: check if PASSWORD contains symbols, capital letters, numbers AND check password length
    //TODO: check if NAMES does not contain any special characters and numbers
}
