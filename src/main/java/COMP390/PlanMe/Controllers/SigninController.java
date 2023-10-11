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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class SigninController {
    private UserDAO userDAO;

    private HomepageDAO homepageDAO;
    @Autowired
    public SigninController(UserDAO userDAO, HomepageDAO homepageDAO){
        this.userDAO = userDAO;
        this.homepageDAO = homepageDAO;
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
        //First and last name validation
        if (!isNameOk(user.getFirstName())) {
            model.addAttribute("nameError", "Name fields cannot contain any numbers and special characters.");
            return "signin";
        }
        if (!isNameOk(user.getLastName())) {
            model.addAttribute("nameError", "Name fields cannot contain any numbers and special characters.");
            return "signin";
        }
        //Password validation
        if (!isValidPassword(user.getPassword())) {
            model.addAttribute("passwordError", "Password must be between 10 and 12 characters");
            return "signin";
        }

        if (!isPasswordOk(user.getPassword())) {
            model.addAttribute("passwordError", "Password must contain at least 1 capital letter and special characters.");
            return "signin";
        }
        //emial validation
        if (!isValidEmail(user.getEmail())) {
            model.addAttribute("emailError", "Invalid email format");
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
        return "redirect:/login";
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private boolean isValidPassword(String password) {
        int passwordLength = password.length();
        return passwordLength >= 10 && passwordLength <= 12;
    }

    private boolean isPasswordOk(String password){
        String pattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(password);
        return matcher.matches();
    }

    private boolean isNameOk(String name){
        String pattern = "^[a-zA-Z]+$";
        return name.matches(pattern);
    }
}
