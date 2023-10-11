package COMP390.PlanMe.Controllers;

import COMP390.PlanMe.dao.*;
import COMP390.PlanMe.entity.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Controller
public class ProjectController {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;
    private final barDAO barDAO;

    @Autowired
    public ProjectController(ProjectDAO projectDAO, UserDAO userDAO, TaskDAO taskDAO, barDAO barDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
        this.barDAO = barDAO;
    }
    //-----------------------------------------------------PROJECT METHODS-----------------------------------------
    @GetMapping("/project/new")
    public String showNewProjectForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("project", new Project());
        return "new-project";
    }

    @PostMapping("/project/new")
    public String createProject(@RequestParam("name") String name, HttpSession session, Model model) {
        User creator = (User) session.getAttribute("user");
        if (creator == null) {
            return "redirect:/login";
        }
        Project project = new Project();
        if (isNameEmpty(name)) {
            model.addAttribute("nameError", "Project name cannot be empty");
            model.addAttribute("project", project);
            return "new-project";
        }

        project.setName(name);
        Bar bar1 = new Bar();
        bar1.setName("TODO");
        bar1.setPosition(1);

        Bar bar2 = new Bar();
        bar2.setName("IN PROGRESS");
        bar2.setPosition(2);

        Bar bar3 = new Bar();
        bar3.setName("DONE");
        bar3.setPosition(3);

        // Set the project for each bar
        bar1.setProject(project);
        bar2.setProject(project);
        bar3.setProject(project);

        barDAO.save(bar1);
        barDAO.save(bar2);
        barDAO.save(bar3);

        project.setCreator(creator);
        project.getMembers().add(creator);
        project.getBars().add(bar1);
        project.getBars().add(bar2);
        project.getBars().add(bar3);
        projectDAO.save(project);

        session.setAttribute("newProject", project);

        return "redirect:/homepage";
    }
    @DeleteMapping("/project/deleteProject")
    public ResponseEntity<Void> deleteBar(@RequestParam("projectId") Long projectId){
        Project project = projectDAO.getProjectById(projectId);
        if(project != null){
            projectDAO.delete(project);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/projects")
    public String viewProjects(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        List<Project> projects = projectDAO.getProjectsByUser(user);
        model.addAttribute("projects", projects);
        return "projects";
    }
    @GetMapping("/projects/{projectId}")
    public String viewProject(@PathVariable("projectId") Long projectId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        Project project = projectDAO.getProjectById(projectId);
        if (project == null) {
            return "redirect:/projects";
        }

        // Retrieve the user details and add them to the model
        User userDetail = userDAO.getUserByEmail(user.getEmail());
        model.addAttribute("user", userDetail);

        model.addAttribute("project", project);
        model.addAttribute("bars", project.getBars());  // This will add the list of bars to the model
        return "project-details";
    }
    //-----------------------------------------------------Chat METHODS-----------------------------------------

    //Methods
    private boolean isNameEmpty(String name){
        return name == null || name.trim().isEmpty();
    }
}
