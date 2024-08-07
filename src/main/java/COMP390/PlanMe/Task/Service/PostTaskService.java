package COMP390.PlanMe.Task.Service;

import COMP390.PlanMe.Bar.BarDAO;
import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.Bar.Bar;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Exceptions.BadArgumentException;
import COMP390.PlanMe.Exceptions.NotFoundException;
import COMP390.PlanMe.Task.Task;
import COMP390.PlanMe.Task.TaskDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.ws.rs.InternalServerErrorException;

@Service
@AllArgsConstructor
public class PostTaskService {
    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;
    private final BarDAO barDAO;

    public final Void addDescription(Long taskId, String description){
        try{
            Task task = taskDAO.getTaskById(taskId);
            task.setDescription(description);
            taskDAO.save(task);
            // notificationService.notifyUsersOfUpdate();
        }catch (Exception e){
            throw new InternalServerErrorException();
        }
        return null;
    }

    public final String addTask(String taskName, int priority, Long barId) {
        //Adding task to the bar with specific position
        Project project = projectDAO.getProjectByBarId(barId);
        if (project != null) {
            Bar targetBar = project.getBars().stream()
                    .filter(bar -> bar.getId().equals(barId)).findFirst().orElse(null);
            if (targetBar == null) {
                throw new NotFoundException("Bar was not found.");
            }
            Task newTask = new Task();
            if(taskName.equals("")){
                throw new BadArgumentException("Task name cannot be empty.");
            }
            newTask.setName(taskName);
            newTask.setBar(targetBar);
            newTask.setPriority(priority);
            newTask.setState(targetBar.getName());
            newTask.setProject(project);
            newTask.setPosition((long) (targetBar.getTasks().size() + 1));
            targetBar.getTasks().add(newTask);
            taskDAO.save(newTask);
            barDAO.save(targetBar);
            return taskName;
        }
        throw new NotFoundException("Project was not found.");
    }


}
