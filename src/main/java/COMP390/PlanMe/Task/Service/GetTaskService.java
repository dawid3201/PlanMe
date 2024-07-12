package COMP390.PlanMe.Task.Service;

import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.Task.TaskDAO;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Task.Task;
import COMP390.PlanMe.User.User;
import COMP390.PlanMe.Exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class GetTaskService {
    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;

    public final String newName(Long taskId){
        Task task = taskDAO.getTaskById(taskId);
        if(task != null){
            return task.getName();
        }
        throw new NotFoundException("Task was not found.");
    }

    public final Map<Long, String> searchTask(Long projectId, String taskName){
        Project project = projectDAO.getProjectById(projectId);
        HashMap<Long, String> map = new HashMap<>();
        if(project != null){
            List<Task> tasks = project.getTasks();
            for(Task task : tasks){
                if(task.getName().toLowerCase().contains(taskName.toLowerCase())){
                    map.put(task.getId(), task.getName());
                }
            }
        }
        return map;
    }

    public final Map<Long, String> getTaskByUserEmail(Long projectId, String userEmail){
        Project project = projectDAO.getProjectById(projectId);
        HashMap<Long, String> map = new HashMap<>();
        if(project != null){
            List<Task> tasks = project.getTasks();
            for(Task task : tasks){
                if(task.getAssignedUser() != null && task.getAssignedUser().getEmail().equals(userEmail)){
                    map.put(task.getId(), task.getAssignedUser().getEmail());
                }
            }
        }
        return map;
    }

    public final String getTaskName(Long taskId) {
        Task task = taskDAO.findById(taskId).orElseThrow(() -> new NotFoundException("Task with ID "+ taskId + " not found."));
        return task.getName();
    }

    public final String getAssignedTasks(String userEmail, Long projectId) {
        Project project = projectDAO.getProjectById(projectId);

        if (project != null) {
            List<Task> tasksForProject = project.getTasks();
            int i = 1;
            List<String> assignedTasksNames = new ArrayList<>();
            for (Task task : tasksForProject) {
                User assignedUser = task.getAssignedUser();
                if (assignedUser != null && task.getAssignedUser().getEmail().contains(userEmail)) {
                    assignedTasksNames.add(i + " : " + task.getName() + " | P:"+ task.getPriority() + "\n");
                    i++;
                }
            }
            return String.join("\n", assignedTasksNames);
        } else {
            throw new NotFoundException("Project was not found.");
        }
    }
}
