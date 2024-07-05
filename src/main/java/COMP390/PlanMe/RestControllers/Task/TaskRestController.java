package COMP390.PlanMe.RestControllers.Task;

import COMP390.PlanMe.Services.Task.DeleteTaskService;
import COMP390.PlanMe.Services.Task.GetTaskService;
import COMP390.PlanMe.Services.Task.PatchTaskService;
import COMP390.PlanMe.Services.Task.PostTaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/task")
@AllArgsConstructor
public class TaskRestController {
    private DeleteTaskService deleteTaskService;
    private GetTaskService getTaskService;
    private PatchTaskService patchTaskService;
    private PostTaskService postTaskService;
    //-------------------------------------DELETE-METHOD-----------------------------------
    @DeleteMapping("/removeTask")
    public ResponseEntity<Void> removeTask(@RequestParam("taskId") Long taskId) {
        return ResponseEntity.ok(deleteTaskService.removeTask(taskId));
    }
    //-------------------------------------GET-METHOD-----------------------------------
    @GetMapping("/newName/{taskId}")
    public ResponseEntity<String> newName(@PathVariable("taskId") Long taskId){
            return ResponseEntity.ok(getTaskService.newName(taskId));
    }
    @GetMapping("/findTask")
    public ResponseEntity<Map<Long, String>> searchTask(@RequestParam("projectId") Long projectId,
                                                              @RequestParam("taskName") String taskName){
        return ResponseEntity.ok(getTaskService.searchTask(projectId, taskName));
    }
    @GetMapping("/getTaskByUser")
    public ResponseEntity<Map<Long, String>> getTaskByUserEmail(@RequestParam("projectId") Long projectId,
                                                                      @RequestParam("userEmail") String userEmail){
        return ResponseEntity.ok(getTaskService.getTaskByUserEmail(projectId, userEmail));
    }
    @GetMapping("/getTaskName/{taskId}")
    public ResponseEntity<String> getTaskName(@PathVariable Long taskId) {
        return ResponseEntity.ok(getTaskService.getTaskName(taskId));
    }
    @GetMapping("/ListOfTasks")
    public ResponseEntity<String> getAssignedTasks(@RequestParam("userEmail") String userEmail,
                                                         @RequestParam("projectId") Long projectId) {
        return ResponseEntity.ok(getTaskService.getAssignedTasks(userEmail, projectId));
    }
    //-------------------------------------PATCH-METHOD-----------------------------------
    @PatchMapping("/updateTaskPosition")
    public ResponseEntity<Long> updateTaskPosition(@RequestParam("taskId") Long taskId,
                                                         @RequestParam("newPosition") Long newPosition,
                                                         @RequestParam("barId") Long barId) {
        return ResponseEntity.ok(patchTaskService.updateTaskPosition(taskId, newPosition, barId));
    }
    @PatchMapping("/updateTaskName")
    public ResponseEntity<Void> updateTaskName(@RequestParam("taskId") Long taskId, @RequestParam("taskName") String taskName){
        return ResponseEntity.ok(patchTaskService.updateTaskName(taskId, taskName));
    }
    @PatchMapping("/updateTaskPriority")
    public ResponseEntity<Void> updateTaskPriority(@RequestParam ("taskId") Long taskId, @RequestParam ("newTaskPriority") int newPriority) {
        return ResponseEntity.ok(patchTaskService.updateTaskPriority(taskId, newPriority));
    }
    //-------------------------------------POST-METHOD-----------------------------------
    @PostMapping("/addTask")
    public ResponseEntity<String> addTask(
            @RequestParam("projectId") Long projectId,
            @RequestParam("taskName") String taskName,
            @RequestParam("taskPriority") int priority,
            @RequestParam("barId") Long barId) {
        return ResponseEntity.ok(postTaskService.addTask(projectId, taskName, priority, barId));
    }
    //Adding new description to a task
    @PostMapping("/addDescription")
    public ResponseEntity<Void> addDescription(@RequestParam("taskId")Long taskId, @RequestParam("description") String description){
        return ResponseEntity.ok(postTaskService.addDescription(taskId, description));
    }
}
