package COMP390.PlanMe.Controllers;

import COMP390.PlanMe.dao.*;
import COMP390.PlanMe.entity.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ProjectController {
    private ProjectDAO projectDAO;
    private UserDAO userDAO;
    private TaskDAO taskDAO;
    private barDAO barDAO;

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

        // Save the bars
        barDAO.save(bar1);
        barDAO.save(bar2);
        barDAO.save(bar3);

        project.setCreator(creator);
        project.getMembers().add(creator);
        project.getBars().add(bar1);
        project.getBars().add(bar2);
        project.getBars().add(bar3);
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
        model.addAttribute("project", project);
        model.addAttribute("bars", project.getBars());  // This will add the list of bars to the model
        return "project-details";
    }

    //----------------------------------------------BAR METHODS----------------------------------------------
    @GetMapping("/project/addBar")
    public ResponseEntity<Void> addBar(@RequestParam("projectId") Long projectId, @RequestParam("barName") String barName) {
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            Bar newBar = new Bar();

            newBar.setName(barName);
            newBar.setPosition(project.getBars().size() + 1);
            newBar.setProject(project);
            barDAO.save(newBar);
            project.getBars().add(newBar);
            projectDAO.save(project);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    //---------------------------------------------------MEMBERS METHODS-----------------------------------------
    //TODO: update this method to add members to a project
//    @PostMapping("/project/addMember")
//    public String addMemberToProject(@RequestParam("projectId") Long projectId, @RequestParam("memberEmail") String memberEmail) {
//        User newMember = userDAO.getUserByEmail(memberEmail);
//        Project project = projectDAO.getProjectById(projectId);
//        if (newMember != null && project != null) {
//            project.getMembers().add(newMember);
//            projectDAO.save(project);
//        }
//        return "redirect:/homepage";
//    }

    //----------------------------------------------TASK METHODS----------------------------------------------
    @PostMapping("/project/addTask")
    public ResponseEntity<Void> addTask(@RequestParam("projectId") Long projectId, @RequestParam("taskDescription") String taskDescription, @RequestParam("taskPriority") int priority) {
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            Bar todoBar = project.getBars().stream().filter(bar -> "TODO".equals(bar.getName())).findFirst().orElse(null);
            if (todoBar == null) {
                return ResponseEntity.notFound().build();
            }
            Task newTask = new Task();
            newTask.setDescription(taskDescription);
            newTask.setBar(todoBar);
            newTask.setPriority(priority);
            newTask.setState(todoBar.getName());
            newTask.setProject(project);
            newTask.setPosition((long) (todoBar.getTasks().size() + 1));  // set the position
            todoBar.getTasks().add(newTask); // add the task to the bar
            taskDAO.save(newTask); // save the task to the database
            barDAO.save(todoBar); // save the bar to the database

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/project/updateTaskName")
    public ResponseEntity<Void> updateTaskName(@RequestParam("taskId") Long taskId, @RequestParam("taskDescription") String taskDescription){
        try {
            Task task = taskDAO.getOne(taskId);
            task.setDescription(taskDescription);
            taskDAO.save(task); // Save changes to the database
            return ResponseEntity.ok().build(); // Return a 200 OK response
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // Return a 404 Not Found response if the task doesn't exist
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return a 500 Internal Server Error response for any other exceptions
        }
    }
    @GetMapping("/project/getTasks")
    public ResponseEntity<List<Task>> getTasks(@RequestParam("projectId") Long projectId) {
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            List<Task> tasks = project.getTasks();  // Fetches the tasks for the project
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PatchMapping("/project/updateTaskSwimlane")
    public ResponseEntity<Void> updateTaskSwimlane(@RequestParam("taskId") Long taskId, @RequestParam("newSwimlane") String newSwimlane, @RequestParam("barId") Long barId) {
        Task task = taskDAO.getOne(taskId);
        Bar bar = barDAO.getBarById(barId);
        task.setBar(bar);
        task.setState(bar.getName());
        taskDAO.save(task);
        return ResponseEntity.ok().build();
    }

    //TODO: update this method to update the priority of a task
    //@PatchMapping("/project/updateTaskPriority")

    @PatchMapping("/project/updateTaskPosition")
    public ResponseEntity<Void> updateTaskPosition(@RequestParam("taskId") Long taskId, @RequestParam("newPosition") Long newPosition) {
        try {
            Task task = taskDAO.getOne(taskId);
            Bar bar = task.getBar();
            List<Task> tasks = bar.getTasks();
            tasks.remove(task);
            tasks.add(newPosition.intValue() - 1, task);
            for (int i = 0; i < tasks.size(); i++) {
                tasks.get(i).setPosition((long) (i + 1));
            }
            bar.setTasks(tasks);
            barDAO.save(bar);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/project/removeTask")
    public ResponseEntity<Void> removeTask(@RequestParam("taskId") Long taskId) {
        try {
            Task task = taskDAO.getOne(taskId);
            Bar bar = task.getBar();
            bar.getTasks().remove(task);
            barDAO.save(bar);
            taskDAO.delete(task);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //Methods
    private boolean isNameEmpty(String name){
        return name == null || name.trim().isEmpty();
    }
}
