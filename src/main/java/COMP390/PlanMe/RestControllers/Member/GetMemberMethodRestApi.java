package COMP390.PlanMe.RestControllers.Member;

import COMP390.PlanMe.Exceptions.NotFoundException;
import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Dao.UserDAO;
import COMP390.PlanMe.Entity.Project;
import COMP390.PlanMe.Entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GetMemberMethodRestApi {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;

    public GetMemberMethodRestApi(ProjectDAO projectDAO, UserDAO userDAO, TaskDAO taskDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
    }
    @GetMapping("/getMembers/{projectId}") //Used in PROJECT.JS
    public ResponseEntity<String> getTaskName(@PathVariable Long projectId) {
        Project project = projectDAO.getProjectById(projectId);
        if(project != null){
            List<String> userNames = new ArrayList<>();
            for(User user : project.getMembers()){
                userNames.add(user.getFirstName() + " " + user.getLastName() + " | " + user.getEmail());
            }
            String joinNames = String.join("\n", userNames);
            return ResponseEntity.ok(joinNames);
        }
        return ResponseEntity.notFound().build();
    }
    @GetMapping("/getInitials/{email}") //USed in TASK.JS
    public ResponseEntity<String> getInitials(@PathVariable String email){
        User user = userDAO.findByEmail(email);
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
