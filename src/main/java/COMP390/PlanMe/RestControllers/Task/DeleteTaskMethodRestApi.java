package COMP390.PlanMe.RestControllers.Task;

import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DeleteTaskMethodRestApi{
    private final TaskDAO taskDAO;
    private final BarDAO barDAO;

    @Autowired
    public DeleteTaskMethodRestApi(TaskDAO taskDAO, BarDAO barDAO) {
        this.taskDAO = taskDAO;
        this.barDAO = barDAO;
    }
    @DeleteMapping("/project/removeTask")
    public final ResponseEntity<Void> removeTask(@RequestParam("taskId") Long taskId) {
        try {
            Task task = taskDAO.getTaskById(taskId);
            Bar bar = task.getBar();
            bar.getTasks().remove(task);
            barDAO.save(bar);
            taskDAO.delete(task);
          //  notificationService.notifyUsersOfUpdate();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}