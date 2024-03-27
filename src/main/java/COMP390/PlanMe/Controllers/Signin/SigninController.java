package COMP390.PlanMe.Controllers.Signin;

import COMP390.PlanMe.Annotation.PasswordValidator;
import COMP390.PlanMe.Dao.HomepageDAO;
import COMP390.PlanMe.Dao.UserDAO;
import COMP390.PlanMe.Entity.Homepage;
import COMP390.PlanMe.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import javax.validation.Valid;

@Controller
public class SigninController {
    private UserDAO userDAO;
    private PasswordValidator passwordValidator = new PasswordValidator();

    private HomepageDAO homepageDAO;
    @Autowired
    public SigninController(UserDAO userDAO, HomepageDAO homepageDAO){
        this.userDAO = userDAO;
        this.homepageDAO = homepageDAO;
    }

    @GetMapping("/signin")
    private String showSigninForm(){
        return "Signin";
    }
    @PostMapping("/signin")
    private String processSigninForm(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "Signin";
        }
        //First and last name validation
        if (!isNameOk(user.getFirstName())) {
            model.addAttribute("nameError", "Name fields cannot contain any numbers and special characters.");
            return "Signin";
        }
        if (!isNameOk(user.getLastName())) {
            model.addAttribute("nameError", "Name fields cannot contain any numbers and special characters.");
            return "Signin";
        }
        if(!passwordValidator.isValid(user.getPassword(), null)){
            model.addAttribute("passwordError", "Password must be between 10-12 characters and must contain 1 capital letter and 1 symbol.");
            return "Signin";
        }
        //emial validation
        if (!isValidEmail(user.getEmail())) {
            model.addAttribute("emailError", "Invalid email format");
            return "Signin";
        }
        boolean emailExists = userDAO.checkEmailExists(user.getEmail());
        if (emailExists) {
            model.addAttribute("emailError", "Email already exists");
            return "Signin";
        }
        String encryptedPassword = BCEncryption(user.getPassword());
        user.setPassword(encryptedPassword);
        userDAO.save(user);
        System.out.println(user.getPassword());
        Homepage homepage = new Homepage();
        homepage.setUser(user);
        homepageDAO.save(homepage);
        return "redirect:/login";
    }
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    private boolean isNameOk(String name){
        String pattern = "^[a-zA-Z]+$";
        return name.matches(pattern);
    }
    private String BCEncryption(String password){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }
}
