package COMP390.PlanMe.Controllers;

import COMP390.PlanMe.dao.ProjectDAO;
import COMP390.PlanMe.dao.UserDAO;
import COMP390.PlanMe.entity.Project;
import COMP390.PlanMe.entity.User;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProjectController {
    private ProjectDAO projectDAO;
    private UserDAO userDAO;

    public ProjectController(ProjectDAO projectDAO, UserDAO userDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
    }

    @GetMapping("/project/new")
    public String showNewProjectForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("project", new Project());
        return "new-project";  // This should be the name of the Thymeleaf template for creating a new project
    }

    @PostMapping("/project/new")
    public String createProject(@RequestParam("name") String name, HttpSession session) {
        User creator = (User) session.getAttribute("user");
        if (creator == null) {
            return "redirect:/login";
        }
        Project project = new Project();
        project.setName(name);
        project.setCreator(creator);
        project.getMembers().add(creator);
        projectDAO.save(project);

        // Add the project to the session
        session.setAttribute("newProject", project);

        return "redirect:/homepage";
    }



    @PostMapping("/project/save")
    public String saveProject(@ModelAttribute Project project, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        project.setCreator(user);
        projectDAO.save(project);
        return "redirect:/";
    }


    @GetMapping("/projects")
    public String viewProjects(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Project> projects = projectDAO.getProjectsByUser(user);
        model.addAttribute("projects", projects);
        return "projects";  // This should be the name of the Thymeleaf template for viewing projects
    }

    @PostMapping("/project/addMember")
    public String addMemberToProject(@RequestParam("projectId") Long projectId, @RequestParam("memberEmail") String memberEmail) {
        User newMember = userDAO.getUserByEmail(memberEmail);
        Project project = projectDAO.getProjectById(projectId);
        if (newMember != null && project != null) {
            project.getMembers().add(newMember);
            projectDAO.save(project);
        }
        return "redirect:/homepage";
    }
}
