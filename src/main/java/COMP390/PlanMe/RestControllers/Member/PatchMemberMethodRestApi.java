package COMP390.PlanMe.RestControllers.Member;

import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Dao.UserDAO;
import COMP390.PlanMe.Entity.Task;
import COMP390.PlanMe.Entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
public class PatchMemberMethodRestApi {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;

    public PatchMemberMethodRestApi(ProjectDAO projectDAO, UserDAO userDAO, TaskDAO taskDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
    }

    @PatchMapping("/project/assignUserToTask")
    public final ResponseEntity<String> assignUser(@RequestParam("userEmail") String userEmail, @RequestParam("taskId") Long taskId) {
        Task task = taskDAO.getTaskById(taskId);
        if ("Undefined".equalsIgnoreCase(userEmail)) {
            task.setAssignedUser(null); // Assigning the task to no one
        } else {
            User user = userDAO.findByEmail(userEmail);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
            }
            if(user.getTasksAssigned().contains(task)){//Exception 500
                throw new IllegalStateException("User " + user.getEmail() + " is already assign to the task " + task.getName());
            }
            task.setAssignedUser(user);
        }
        taskDAO.save(task);
        System.out.println("Task with ID " + taskId + " assigned to user with email " + userEmail);
        return ResponseEntity.ok().build();
    }
}