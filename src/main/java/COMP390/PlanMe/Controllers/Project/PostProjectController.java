package COMP390.PlanMe.Controllers.Project;

import COMP390.PlanMe.Dao.*;
import COMP390.PlanMe.Entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class PostProjectController {
    private final ProjectDAO projectDAO;
    private final BarDAO barDAO;

    @Autowired
    public PostProjectController(ProjectDAO projectDAO, BarDAO barDAO) {
        this.projectDAO = projectDAO;
        this.barDAO = barDAO;
    }
    //-----------------------------------------------------PROJECT METHODS-----------------------------------------
    @PostMapping("/project/new")
    public final String createProject(@RequestParam("name") String name, @RequestParam("deadline") String deadline, HttpSession session, Model model) {
        User creator = (User) session.getAttribute("user");
        if (creator == null) {
            return "redirect:/login";
        }
        Project project = new Project();
        if (isParamEmpty(name) || isParamEmpty(deadline)) {
            model.addAttribute("nameError", "Input name and deadline");
            model.addAttribute("project", project);
            return "New-project";
        }
        project.setName(name);
        project.setDeadline(deadline);
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
    //Methods
    private boolean isParamEmpty(String name){
        return name == null || name.trim().isEmpty();
    }
    //-----------------------------------------------------Chat METHODS-----------------------------------------

}