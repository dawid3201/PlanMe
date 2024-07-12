package COMP390.PlanMe.Project.Controller;

import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.User.User;
import COMP390.PlanMe.User.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class GetProjectController {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;

    @Autowired
    public GetProjectController(ProjectDAO projectDAO, UserDAO userDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
    }
    //-----------------------------------------------------PROJECT METHODS-----------------------------------------
    @GetMapping("/project/new")
    public final String showNewProjectForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "Homepage";
    }
    @GetMapping("/projects")
    public final String viewProjects(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Project> projects = projectDAO.getProjectsByUser(user);
        model.addAttribute("projects", projects);
        return "projects";
    }
    @GetMapping("/projects/{projectId}")
    public final String viewProject(@PathVariable("projectId") Long projectId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Project project = projectDAO.getProjectById(projectId);
        if (project == null) {
            return "redirect:/projects";
        }

        // Retrieve the user details and add them to the model
        User userDetail = userDAO.findByEmail(user.getEmail());
        model.addAttribute("user", userDetail);

        model.addAttribute("project", project);
        model.addAttribute("bars", project.getBars());  // This will add the list of bars to the model
        return "Project-details";
    }
}
