package COMP390.PlanMe.User.ProjectMember.Service;

import COMP390.PlanMe.Exceptions.TaskNotFoundException;
import COMP390.PlanMe.Exceptions.UserAlreadyAssignedException;
import COMP390.PlanMe.Exceptions.UserNotFoundException;
import COMP390.PlanMe.Task.TaskDAO;
import COMP390.PlanMe.User.UserDAO;
import COMP390.PlanMe.Task.Task;
import COMP390.PlanMe.User.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class PatchMemberService {
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;

    public final String assignUser(String userEmail, Long taskId) throws UserNotFoundException, UserAlreadyAssignedException, TaskNotFoundException {
        Task task = taskDAO.getTaskById(taskId);
        if(task != null){
            if ("Undefined".equalsIgnoreCase(userEmail)) {
                task.setAssignedUser(null); // Assigning the task to no one
            } else {
                User user = userDAO.findByEmail(userEmail);
                if (user == null) {
                    throw new UserNotFoundException("User was found.");
                }
                if(isUserAlreadyAssignedToTask(user, task)){//Exception 500
                    throw new UserAlreadyAssignedException("User " + user.getEmail() + " is already assign to the task " + task.getName());
                }
                task.setAssignedUser(user);
            }
            taskDAO.save(task);
            return "Task with ID " + taskId + " assigned to user with email " + userEmail;
        }
        return "Assigning user was not successful";
    }
    private boolean isUserAlreadyAssignedToTask(User user, Task task) {
        return user.getTasksAssigned() != null && user.getTasksAssigned().contains(task);
    }
}
