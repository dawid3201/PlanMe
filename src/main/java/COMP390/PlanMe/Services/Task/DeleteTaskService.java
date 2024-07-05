package COMP390.PlanMe.Services.Task;

import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Entity.Task;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.ws.rs.InternalServerErrorException;

@Service
@AllArgsConstructor
public class DeleteTaskService {
    private final TaskDAO taskDAO;
    private final BarDAO barDAO;

    public final Void removeTask(Long taskId) {
        try {
            Task task = taskDAO.getTaskById(taskId);
            Bar bar = task.getBar();
            bar.getTasks().remove(task);
            barDAO.save(bar);
            taskDAO.delete(task);
            //  notificationService.notifyUsersOfUpdate();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        return null;
    }
}
