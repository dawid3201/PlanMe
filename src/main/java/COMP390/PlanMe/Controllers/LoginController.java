package COMP390.PlanMe.Controllers;

import COMP390.PlanMe.dao.ProjectDAO;
import COMP390.PlanMe.entity.Homepage;
import COMP390.PlanMe.dao.HomepageDAO;
import COMP390.PlanMe.dao.UserDAO;
import COMP390.PlanMe.entity.Project;
import COMP390.PlanMe.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LoginController {
    private UserDAO userDAO;
    private HomepageDAO homepageDAO;
    private ProjectDAO projectDAO;
    @Autowired
    public LoginController(UserDAO userDAO, HomepageDAO homepageDAO, ProjectDAO projectDAO) {
        this.userDAO = userDAO;
        this.homepageDAO = homepageDAO;
        this.projectDAO = projectDAO;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    @PostMapping("/login")
    private String processLoginForm(@RequestParam("email") String email,@RequestParam("password") String password, Model model, HttpSession session) {
        boolean emailExists = userDAO.checkEmailExists(email);
        if (emailExists) {
            // Perform login logic
            User user = userDAO.getUserByEmail(email);
            if(user.getPassword().equals(password)) { // check users password when logging in
                session.setAttribute("user", user);
                return "redirect:/homepage";
            } else {
                model.addAttribute("error", "Incorrect password");
                return "login";
            }
        } else {
            model.addAttribute("error", "Incorrect email");
            return "login";
        }
    }
    @GetMapping("/homepage")
    public String showDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Homepage homepage = homepageDAO.getHomepageByUser(user);
            model.addAttribute("user", user);
            model.addAttribute("homepage", homepage);

            // Retrieve all projects of the user from the database
            List<Project> projects = projectDAO.getProjectsByUser(user);
            model.addAttribute("projects", projects);
        } else {
            // Redirect to login page if no user is logged in
            return "redirect:/login";
        }
        return "homepage";
    }

    @PostMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
