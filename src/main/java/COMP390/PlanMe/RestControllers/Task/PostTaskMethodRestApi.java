package COMP390.PlanMe.RestControllers.Task;

import COMP390.PlanMe.Exceptions.BadArgumentException;
import COMP390.PlanMe.Services.NotificationService;
import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Entity.Project;
import COMP390.PlanMe.Entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostTaskMethodRestApi {
    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;
    private final BarDAO barDAO;

    @Autowired
    public PostTaskMethodRestApi(ProjectDAO projectDAO, TaskDAO taskDAO, BarDAO barDAO) {
        this.projectDAO = projectDAO;
        this.taskDAO = taskDAO;
        this.barDAO = barDAO;
    }
    @PostMapping("/project/addTask")
    public final ResponseEntity<String> addTask(
            @RequestParam("projectId") Long projectId,
            @RequestParam("taskName") String taskName,
            @RequestParam("taskPriority") int priority,
            @RequestParam("barId") Long barId) {
        //Adding task to the bar with specific position
        Project project = projectDAO.getProjectById(projectId);
        System.out.println("Received taskName: " + taskName);
        if (project != null) {
            Bar targetBar = project.getBars().stream()
                    .filter(bar -> bar.getId().equals(barId)).findFirst().orElse(null);
            if (targetBar == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bar does not exist");
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
            return ResponseEntity.ok().body(taskName);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project does not exist");
    }
    //Adding new description to a task
    @PostMapping("/project/addDescription")
    public final ResponseEntity<Void> addDescription(@RequestParam("taskId")Long taskId, @RequestParam("description") String description){
        try{
            Task task = taskDAO.getTaskById(taskId);
            task.setDescription(description);
            taskDAO.save(task);
           // notificationService.notifyUsersOfUpdate();
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}