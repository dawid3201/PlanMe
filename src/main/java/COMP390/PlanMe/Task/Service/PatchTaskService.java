package COMP390.PlanMe.Task.Service;

import COMP390.PlanMe.Bar.BarDAO;
import COMP390.PlanMe.Bar.Bar;
import COMP390.PlanMe.Exceptions.BadArgumentException;
import COMP390.PlanMe.Exceptions.NotFoundException;
import COMP390.PlanMe.Task.Task;
import COMP390.PlanMe.Task.TaskDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.ws.rs.InternalServerErrorException;
import java.util.List;

@Service
@AllArgsConstructor
public class PatchTaskService {
    private final TaskDAO taskDAO;
    private final BarDAO barDAO;

    public final Void updateTaskPriority(Long taskId, int newPriority) {
        try{
            Task task = taskDAO.getTaskById(taskId);
            task.setPriority(newPriority);
            taskDAO.save(task);
            // notificationService.notifyUsersOfUpdate();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
        return null;
    }

    public final Void updateTaskName(Long taskId, String taskName){
        Task task = taskDAO.getTaskById(taskId);
        if(!taskName.isEmpty()){
            task.setName(taskName);
            System.out.println("task with name " + taskName + " upadted");
            taskDAO.save(task);
        }else{
            throw new BadArgumentException("Task name cannot be empty");
        }
        return null;
    }

    public final Long updateTaskPosition(Long taskId, Long newPosition,Long barId) {
        try {
            Task task = taskDAO.getTaskById(taskId);
            if(task == null){
                throw new NotFoundException("Task was not found.");
            }
            Bar originalBar = task.getBar();
            if(originalBar != null) {
                removeTaskAndUpdateBar(task, originalBar);
            }else{
                throw new NotFoundException("Bar does not exist");
            }
            return UpdateTaskPositionWithinBar(task, newPosition, barId);

        } catch (Exception e) {
            System.out.println("Error while updating task position: " + e.getMessage());
            e.printStackTrace();
            throw new InternalServerErrorException();
        }
    }
    //----------------------------------------HELPER-METHODS-FOR-UpdateTaskPosition--------------------
    private long UpdateTaskPositionWithinBar(Task task, Long newPosition,Long barId){
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
        return newPosition;
    }
    private void removeTaskAndUpdateBar(Task task, Bar originalBar){
            List<Task> originalBarTasks = originalBar.getTasks();
            originalBarTasks.remove(task);
            reorderTaskPositions(originalBarTasks);
            originalBar.setTasks(originalBarTasks);
            barDAO.save(originalBar);
    }
    private void reorderTaskPositions(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition((long) (i + 1));
        }
    }
}
