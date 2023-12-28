package COMP390.PlanMe.RestControllers.Task;

import COMP390.PlanMe.Exceptions.NotFoundException;
import COMP390.PlanMe.Services.NotificationService;
import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Entity.Task;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class PatchTaskMethodRestApi {
    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;
    private final BarDAO barDAO;
    private final NotificationService notificationService;

    @Autowired
    public PatchTaskMethodRestApi(ProjectDAO projectDAO, TaskDAO taskDAO, BarDAO barDAO, NotificationService notificationService) {
        this.projectDAO = projectDAO;
        this.taskDAO = taskDAO;
        this.barDAO = barDAO;
        this.notificationService = notificationService; // Initialize notificationService here
    }
//    @Transactional
    @PatchMapping("/project/updateTaskPosition")
    public final ResponseEntity<Long> updateTaskPosition(@RequestParam("taskId") Long taskId,
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
            System.out.println("New position for task with ID: " + task.getId() + " is: "+ task.getPosition() + " and barName is: " + newBar.getName());
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
    public final ResponseEntity<Void> updateTaskName(@RequestParam("taskId") Long taskId, @RequestParam("taskName") String taskName){
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
    public final ResponseEntity<Void> updateTaskPriority(@RequestParam ("taskId") Long taskId, @RequestParam ("newTaskPriority") int newPriority) {
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
}