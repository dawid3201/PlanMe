package COMP390.PlanMe.Controllers;

import COMP390.PlanMe.dao.ProjectDAO;
import COMP390.PlanMe.dao.TaskDAO;
import COMP390.PlanMe.dao.UserDAO;
import COMP390.PlanMe.entity.Project;
import COMP390.PlanMe.entity.User;
import COMP390.PlanMe.entity.Task;
import COMP390.PlanMe.entity.TaskState;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ProjectController(ProjectDAO projectDAO, UserDAO userDAO, TaskDAO taskDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
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
        return "project-details";
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
            Task newTask = new Task();
            newTask.setDescription(taskDescription);
            newTask.setState(TaskState.TODO);
            newTask.setSwimlane("TODO");
            newTask.setProject(project);
            newTask.setPriority(priority);
            project.getTasks().add(newTask);  // add the task to the project

            taskDAO.save(newTask);  // save the task to the database
            projectDAO.save(project);  // save the project to the database

            project.getTasks().size();  // Forces Hibernate to fetch the tasks

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
    public ResponseEntity<Void> updateTaskSwimlane(@RequestParam("taskId") Long taskId, @RequestParam("newSwimlane") String newSwimlane) {
        Optional<Task> optionalTask = taskDAO.findById(taskId);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();

            switch (newSwimlane) {
                case "TODO" -> task.setState(TaskState.TODO);
                case "IN_PROGRESS" -> task.setState(TaskState.IN_PROGRESS);
                case "DONE" -> task.setState(TaskState.DONE);
                case "UNRESOLVED" -> task.setState(TaskState.UNRESOLVED);
                default -> {
                    return ResponseEntity.badRequest().build();
                }
            }

            taskDAO.save(task);  // save the task to the database
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }
    @DeleteMapping("/project/removeTask")
    public ResponseEntity<Void> removeTask(@RequestParam("taskId") Long taskId) {
        Optional<Task> task = taskDAO.findById(taskId);
        if (task.isPresent()) {
            taskDAO.delete(task.get());
            return ResponseEntity.ok().build(); // Return a 200 OK response
        } else {
            return ResponseEntity.notFound().build(); // Return a 404 Not Found response if the task doesn't exist
        }
    }
    //Methods
    private boolean isNameEmpty(String name){
        return name == null || name.trim().isEmpty();
    }
}
