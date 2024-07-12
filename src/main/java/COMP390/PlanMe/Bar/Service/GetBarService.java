package COMP390.PlanMe.Bar.Service;

import COMP390.PlanMe.Bar.Bar;
import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class GetBarService {
    private ProjectDAO projectDAO;
    public final List<Bar> updatedTaskList(Long projectId) throws NotFoundException{
            Project project = projectDAO.getProjectById(projectId);
            if (project != null) {
                return project.getBars();
            }
            throw new NotFoundException("Project was not found.");
    }
}
