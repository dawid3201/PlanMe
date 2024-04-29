package COMP390.PlanMe.RestControllers.Task;

import COMP390.PlanMe.Exceptions.BadArgumentException;
import COMP390.PlanMe.Exceptions.NotFoundException;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class PatchTaskMethodRestApi {
    private final TaskDAO taskDAO;
    private final BarDAO barDAO;

    @Autowired
    public PatchTaskMethodRestApi(TaskDAO taskDAO, BarDAO barDAO) {
        this.taskDAO = taskDAO;
        this.barDAO = barDAO;
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
        Task task = taskDAO.getTaskById(taskId);
            if(!taskName.isEmpty()){
                task.setName(taskName);
                System.out.println("task with name " + taskName + " upadted");
                taskDAO.save(task);
            }else{
                throw new BadArgumentException("Task name cannot be empty");
            }
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/project/updateTaskPriority")
    public final ResponseEntity<Void> updateTaskPriority(@RequestParam ("taskId") Long taskId, @RequestParam ("newTaskPriority") int newPriority) {
        try{
            Task task = taskDAO.getTaskById(taskId);
            task.setPriority(newPriority);
            taskDAO.save(task);
           // notificationService.notifyUsersOfUpdate();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}