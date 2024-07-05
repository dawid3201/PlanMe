package COMP390.PlanMe.Services.Member;

import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Dao.UserDAO;
import COMP390.PlanMe.Entity.Task;
import COMP390.PlanMe.Entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.ws.rs.BadRequestException;

@Service
@AllArgsConstructor
public class PatchMemberService {
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;

    public final Boolean assignUser(String userEmail, Long taskId) {
        Task task = taskDAO.getTaskById(taskId);
        if ("Undefined".equalsIgnoreCase(userEmail)) {
            task.setAssignedUser(null); // Assigning the task to no one
        } else {
            User user = userDAO.findByEmail(userEmail);
            if (user == null) {
                throw new BadRequestException("User was found.");
            }
            if(user.getTasksAssigned().contains(task)){//Exception 500
                throw new IllegalStateException("User " + user.getEmail() + " is already assign to the task " + task.getName());
            }
            task.setAssignedUser(user);
        }
        taskDAO.save(task);
        System.out.println("Task with ID " + taskId + " assigned to user with email " + userEmail);
        return true;
    }
}
