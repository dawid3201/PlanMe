package COMP390.PlanMe.Bar.Service;

import COMP390.PlanMe.Bar.Bar;
import COMP390.PlanMe.Bar.BarDAO;
import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.Task.TaskDAO;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Task.Task;
import COMP390.PlanMe.Exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.ws.rs.InternalServerErrorException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PatchBarService {
    private final ProjectDAO projectDAO;
    private final TaskDAO taskDAO;
    private final BarDAO barDAO;


    public final Map<String, Long> updateBarPosition(Long barId,Long newPosition) throws NotFoundException{
        try {
            Bar bar = barDAO.getBarById(barId);
            Project project = bar.getProject();
            List<Bar> bars = barDAO.findByProjectId(project.getId());
            bars.sort(Comparator.comparing(Bar::getPosition));
            bars.remove(bar);
            //Code updated after Unit tests
            if(!bars.isEmpty()){
                bars.add(newPosition.intValue() - 1, bar);
            }else{
                bars.add(bar);
            }
            reorderBarPositions(bars);

            barDAO.saveAll(bars);
            projectDAO.save(project);
            return Map.of("barId", barId, "newPosition", newPosition);

        } catch (Exception e) {
            throw new NotFoundException("Bar was not found.");
        }
    }
    private void reorderBarPositions(List<Bar> bars) {
        for (int i = 0; i < bars.size(); i++) {
            bars.get(i).setPosition(i + 1);
        }
    }
    public final Bar updateBarName(Long barId, String barName) throws NotFoundException{
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
            return bar;
        }
        throw new NotFoundException("Bar with name: " + barName + " does not exist.");
    }

}
