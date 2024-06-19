package COMP390.PlanMe.RestControllers.Task;

import COMP390.PlanMe.Exceptions.NotFoundException;
import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Entity.Project;
import COMP390.PlanMe.Entity.Task;
import COMP390.PlanMe.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class GetTaskMethodRestApi {
    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;

    @Autowired
    public GetTaskMethodRestApi(ProjectDAO projectDAO, TaskDAO taskDAO) {
        this.projectDAO = projectDAO;
        this.taskDAO = taskDAO;
    }
    @GetMapping("/project/newName/{taskId}")
    public final ResponseEntity<String> newName(@PathVariable("taskId") Long taskId){
        Task task = taskDAO.getTaskById(taskId);
        if(task != null){
            return ResponseEntity.ok(task.getName());
        }
        return ResponseEntity.notFound().build();
    }

    //Method for finding task by name in search bar feature
    @GetMapping("/project/findTask")
    public final ResponseEntity<Map<Long, String>> searchTask(@RequestParam("projectId") Long projectId,
                                                              @RequestParam("taskName") String taskName){
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
        return ResponseEntity.ok(map);
    }
    @GetMapping("/project/getTaskByUser")
    public final ResponseEntity<Map<Long, String>> getTaskByUserEmail(@RequestParam("projectId") Long projectId,
                                                           @RequestParam("userEmail") String userEmail){
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
        return ResponseEntity.ok(map);
    }
    @GetMapping("/getTaskName/{taskId}")
    public final ResponseEntity<String> getTaskName(@PathVariable Long taskId) {
        Task task = taskDAO.findById(taskId).orElseThrow(() -> new NotFoundException("Task with ID "+ taskId + " not found."));
        return ResponseEntity.ok(task.getName());

//        Optional<Task> task = taskDAO.findById(taskId);
//        return task.map(value -> ResponseEntity.ok(value.getName())).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/project/ListOfTasks")
    public final ResponseEntity<String> getAssignedTasks(@RequestParam("userEmail") String userEmail, @RequestParam("projectId") Long projectId) {
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
            String joinStrings = String.join("\n", assignedTasksNames);
            return ResponseEntity.ok(joinStrings);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}