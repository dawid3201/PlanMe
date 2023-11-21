package COMP390.PlanMe.RestControllers;

import COMP390.PlanMe.Exceptions.BadArgumentException;
import COMP390.PlanMe.Exceptions.NotFoundException;
import COMP390.PlanMe.Services.NotificationService;
import COMP390.PlanMe.dao.ProjectDAO;
import COMP390.PlanMe.dao.TaskDAO;
import COMP390.PlanMe.dao.UserDAO;
import COMP390.PlanMe.dao.BarDAO;
import COMP390.PlanMe.entity.Bar;
import COMP390.PlanMe.entity.Project;
import COMP390.PlanMe.entity.Task;
import COMP390.PlanMe.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class TaskRestAPI {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;
    private final BarDAO barDAO;
    private final NotificationService notificationService;


    @Autowired
    public TaskRestAPI(ProjectDAO projectDAO, UserDAO userDAO, TaskDAO taskDAO, BarDAO barDAO, NotificationService notificationService) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
        this.barDAO = barDAO;
        this.notificationService = notificationService; // Initialize notificationService here
    }

    @PostMapping("/project/addTask")
    public ResponseEntity<String> addTask(
            @RequestParam("projectId") Long projectId,
            @RequestParam("taskName") String taskName,
            @RequestParam("taskPriority") int priority,
            @RequestParam("barId") Long barId) {
        //Adding task to the bar with specific position
        Project project = projectDAO.getProjectById(projectId);
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
//            notificationService.notifyUsersOfUpdate();
            return ResponseEntity.ok().body(taskName);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project does not exist");
    }
    @GetMapping("/project/newName/{taskId}")
    public ResponseEntity<String> newName(@PathVariable("taskId") Long taskId){
        Task task = taskDAO.getTaskById(taskId);
        if(task != null){
            return ResponseEntity.ok(task.getName());
        }
        return ResponseEntity.notFound().build();
    }
    @Transactional
    @PatchMapping("/project/updateTaskPosition")
    public ResponseEntity<Long> updateTaskPosition(@RequestParam("taskId") Long taskId,
                                                   @RequestParam("newPosition") Long newPosition,
                                                   @RequestParam("barId") Long barId) {
        try {
            Task task = taskDAO.getTaskById(taskId);
            Bar originalBar = task.getBar();
            if(originalBar != null) {//check for exception
                List<Task> originalBarTasks = originalBar.getTasks();
                originalBarTasks.remove(task);
                reorderTaskPositions(originalBarTasks);
                originalBar.setTasks(originalBarTasks);
                barDAO.save(originalBar);
            }else{
                throw new NotFoundException("Task does not exist");
            }
            Bar newBar = barDAO.getBarById(barId);
            List<Task> newBarTasks = newBar.getTasks();

            if (newBarTasks.size() <= newPosition.intValue() - 1) {
                newBarTasks.add(task);
            } else {
                newBarTasks.add(newPosition.intValue() - 1, task);
            }
            reorderTaskPositions(newBarTasks);
            newBar.setTasks(newBarTasks);
            task.setBar(newBar);
            task.setState(newBar.getName());
            taskDAO.save(task);
            barDAO.save(newBar);

            notificationService.taskUpdate();

            return ResponseEntity.ok().body(newPosition);
        } catch (Exception e) {
            System.out.println("Error while updating task position: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private void reorderTaskPositions(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition((long) (i + 1));
        }
    }
    @PatchMapping("/project/updateTaskName")
    public ResponseEntity<Void> updateTaskName(@RequestParam("taskId") Long taskId, @RequestParam("taskName") String taskName){
        try {
            Task task = taskDAO.getTaskById(taskId);
            task.setName(taskName);
            taskDAO.save(task);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("/project/updateTaskPriority")
    public ResponseEntity<Void> updateTaskPriority(@RequestParam ("taskId") Long taskId, @RequestParam ("newTaskPriority") int newPriority) {
        try{
            Task task = taskDAO.getTaskById(taskId);
            task.setPriority(newPriority);
            taskDAO.save(task);
            notificationService.notifyUsersOfUpdate();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/project/removeTask")
    public ResponseEntity<Void> removeTask(@RequestParam("taskId") Long taskId) {
        try {
            Task task = taskDAO.getTaskById(taskId);
            Bar bar = task.getBar();
            bar.getTasks().remove(task);
            barDAO.save(bar);
            taskDAO.delete(task);
            notificationService.notifyUsersOfUpdate();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //Adding new description to a task
    @PostMapping("/project/addDescription")
    public ResponseEntity<Void> addDescription(@RequestParam("taskId")Long taskId, @RequestParam("description") String description){
        try{
            Task task = taskDAO.getTaskById(taskId);
            task.setDescription(description);
            taskDAO.save(task);
            notificationService.notifyUsersOfUpdate();
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //Method for finding task by name in search bar feature
    @GetMapping("/project/findTask")
    public ResponseEntity<Map<Long, String>> searchTask(@RequestParam("projectId") Long projectId, @RequestParam("taskName") String taskName){
        Project project = projectDAO.getProjectById(projectId);
        HashMap<Long, String> map = new HashMap<>();
        if(project != null){
            List<Task> tasks = project.getTasks();
            for(Task task : tasks){
                System.out.println("Retrieved task with name: " + task.getName());
                if(task.getName().toLowerCase().contains(taskName.toLowerCase())){
//                    System.out.println("Looking for tasks containing: " + taskName);
                    map.put(task.getId(), "Task found");
                }
            }
        }
        return ResponseEntity.ok(map);
    }
    @GetMapping("/getTaskName/{taskId}")
    public ResponseEntity<String> getTaskName(@PathVariable Long taskId) {
        Optional<Task> task = taskDAO.findById(taskId);
        return task.map(value -> ResponseEntity.ok(value.getName())).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/project/ListOfTasks")
    public ResponseEntity<String> getAssignedTasks(@RequestParam("userEmail") String userEmail, @RequestParam("projectId") Long projectId) {
        Project project = projectDAO.getProjectById(projectId);

        if (project != null) {
            List<Task> tasksForProject = project.getTasks();
            int i = 1;
            List<String> assignedTasksNames = new ArrayList<>();
            for (Task task : tasksForProject) {
                User assignedUser = task.getAssignedUser();
                if (assignedUser != null && task.getAssignedUser().getEmail().contains(userEmail)) {
                    assignedTasksNames.add(i + " : " + task.getName() + "\n");
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