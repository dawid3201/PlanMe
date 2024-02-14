package COMP390.PlanMe.Controllers.Login;

import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Entity.Homepage;
import COMP390.PlanMe.Dao.HomepageDAO;
import COMP390.PlanMe.Entity.Project;
import COMP390.PlanMe.Entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class GetLoginController {
    private HomepageDAO homepageDAO;
    private ProjectDAO projectDAO;
    @Autowired
    public GetLoginController(HomepageDAO homepageDAO, ProjectDAO projectDAO) {
        this.homepageDAO = homepageDAO;
        this.projectDAO = projectDAO;
    }
    @GetMapping("/login")
    public final String showLoginForm() {
        return "Login";
    }
    @GetMapping("/homepage")
    public final String showDashboard(Model model, HttpSession session) {
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
        return "Homepage";
    }
}