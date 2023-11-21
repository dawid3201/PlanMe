package COMP390.PlanMe.RestControllers;

import COMP390.PlanMe.Exceptions.ConflictException;
import COMP390.PlanMe.Exceptions.NotFoundException;
import COMP390.PlanMe.dao.BarDAO;
import COMP390.PlanMe.dao.ProjectDAO;
import COMP390.PlanMe.dao.TaskDAO;
import COMP390.PlanMe.dao.UserDAO;
import COMP390.PlanMe.entity.Bar;
import COMP390.PlanMe.entity.Project;
import COMP390.PlanMe.entity.Task;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
public class BarRestAPI {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;
    private final BarDAO barDAO;

    public BarRestAPI(ProjectDAO projectDAO, UserDAO userDAO, TaskDAO taskDAO, BarDAO barDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
        this.barDAO = barDAO;
    }
    @Transactional
    @PatchMapping("/project/updateBarPosition")
    public ResponseEntity<Map<String, Long>> updateBarPosition(@RequestParam("barId") Long barId,
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
    @GetMapping("/project/getUpdatedBars/{projectId}")
    public ResponseEntity<List<Bar>> updatedTaskList(@PathVariable("projectId") Long projectId){
        try {
            Project project = projectDAO.getProjectById(projectId);
            if (project != null) {
                return ResponseEntity.ok(project.getBars());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println("Error while fetching tasks: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/project/addBar")
    public ResponseEntity<Bar> addBar(@RequestParam("projectId") Long projectId, @RequestParam("barName") String barName){
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            Bar newBar = new Bar();
            for(Bar oldBar : project.getBars()){
                if(oldBar.getName().equalsIgnoreCase(barName)){
                    throw new ConflictException("There already is a bar with name: " + barName);
                }else{
                    newBar.setName(barName);
                }
            }
            Integer maxPosition = barDAO.getMaxPositionByProject(project);
            newBar.setPosition(maxPosition != null ? maxPosition + 1 : 1);
            newBar.setProject(project);
            barDAO.save(newBar);
            project.getBars().add(newBar);
            projectDAO.save(project);

            return ResponseEntity.ok(newBar);
        }
        return ResponseEntity.notFound().build();
    }
    @PatchMapping("/project/updateBarName")
    public ResponseEntity<Bar> updateBarName(@RequestParam("barId") Long barId, @RequestParam("barName") String barName) {
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
    @DeleteMapping("/project/deleteBar")
    public ResponseEntity<Void> deleteBar(@RequestParam("barId") Long barId) {
        Bar bar = barDAO.getBarById(barId);
        if (bar != null) {
            List<Bar> barsToUpdate = barDAO.getBarsByProjectAndPositionGreaterThan(bar.getProject(), bar.getPosition());
            for(Bar barToUpdate : barsToUpdate){
                barToUpdate.setPosition(barToUpdate.getPosition()-1);
                barDAO.save(barToUpdate);
            }
            barDAO.delete(bar);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}
