package COMP390.PlanMe.RestControllers;

import COMP390.PlanMe.Exceptions.NotFoundException;
import COMP390.PlanMe.dao.ProjectDAO;
import COMP390.PlanMe.dao.TaskDAO;
import COMP390.PlanMe.dao.UserDAO;
import COMP390.PlanMe.entity.Project;
import COMP390.PlanMe.entity.Task;
import COMP390.PlanMe.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MemberRestAPI {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;

    public MemberRestAPI(ProjectDAO projectDAO, UserDAO userDAO, TaskDAO taskDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
    }
    @PostMapping("/project/addMember")
    public ResponseEntity<Void> addMember(@RequestParam("projectId") Long projectId, @RequestParam("memberEmail") String memberEmail) {
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            User member = userDAO.getUserByEmail(memberEmail);
            if (member != null) {
                project.getMembers().add(member);
                projectDAO.save(project);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
    @PatchMapping("/project/assignUserToTask")
    public ResponseEntity<String> assignUser(@RequestParam("userEmail") String userEmail, @RequestParam("taskId") Long taskId) {
        User user = userDAO.getUserByEmail(userEmail);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }
        Task task = taskDAO.getTaskById(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        task.setAssignedUser(user);
        taskDAO.save(task);
        System.out.println("Task with ID" + taskId + " assigned to user with email " + userEmail);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/getMembers/{projectId}") //Used in PROJECT.JS
    public ResponseEntity<String> getTaskName(@PathVariable Long projectId) {
        Project project = projectDAO.getProjectById(projectId);
        if(project != null){
            List<String> userNames = new ArrayList<>();
            for(User u : project.getMembers()){
                userNames.add(u.getFirstName() + " " + u.getLastName() + " | " + u.getEmail());
            }
            String joinNames = String.join("\n", userNames);
            return ResponseEntity.ok(joinNames);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/getInitials/{email}") //USed in TASK.JS
    public ResponseEntity<String> getInitials(@PathVariable String email){
        User user = userDAO.getUserByEmail(email);
        if(user != null){
            String fullName = user.getFirstName() + " " + user.getLastName();
            String[] nameParts = fullName.split("\\s");
            StringBuilder initials  = new StringBuilder();
            for(String namePart : nameParts){
                initials.append(namePart.charAt(0));
            }
            return ResponseEntity.ok(initials.toString().toUpperCase());
        }
        throw new NotFoundException("User with email: " + email + " does not exist.");
    }
}
