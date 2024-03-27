package COMP390.PlanMe.RestControllers.Bar;

import COMP390.PlanMe.Exceptions.NotFoundException;
import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Entity.Project;
import COMP390.PlanMe.Entity.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
public class PatchBarMethodRestApi {
    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;
    private final BarDAO barDAO;

    public PatchBarMethodRestApi(ProjectDAO projectDAO, TaskDAO taskDAO, BarDAO barDAO) {
        this.projectDAO = projectDAO;
        this.taskDAO = taskDAO;
        this.barDAO = barDAO;
    }
    @PatchMapping("/project/updateBarPosition")
    public final ResponseEntity<Map<String, Long>> updateBarPosition(@RequestParam("barId") Long barId,
                                                               @RequestParam("newPosition") Long newPosition) {
        try {
            Bar bar = barDAO.getBarById(barId);
            Project project = bar.getProject();
            List<Bar> bars = barDAO.findByProjectId(project.getId());
            bars.sort(Comparator.comparing(Bar::getPosition));
            bars.remove(bar);
            bars.add(newPosition.intValue() - 1, bar);
            reorderBarPositions(bars);

            barDAO.saveAll(bars);
            projectDAO.save(project);
            return ResponseEntity.ok(Map.of("barId", barId, "newPosition", newPosition));

        } catch (Exception e) {
            System.out.println("Error while updating bar position: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    private void reorderBarPositions(List<Bar> bars) {
        for (int i = 0; i < bars.size(); i++) {
            bars.get(i).setPosition(i + 1);
        }
    }
    @PatchMapping("/project/updateBarName")
    public final ResponseEntity<Bar> updateBarName(@RequestParam("barId") Long barId, @RequestParam("barName") String barName) {
        Bar bar = barDAO.getBarById(barId);
        if (bar != null) {
            String oldBarName = bar.getName();
            bar.setName(barName);
            List<Task> tasks = taskDAO.findAllByState(oldBarName);
            for (Task task : tasks) {
                task.setState(barName);
                taskDAO.save(task);
            }
            barDAO.save(bar);
            return ResponseEntity.ok(bar);
        }
        throw new NotFoundException("Bar with name: " + barName + " does not exist.");
    }
}
