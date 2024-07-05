package COMP390.PlanMe.Services.Bar;

import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Entity.Project;
import COMP390.PlanMe.Exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.ws.rs.InternalServerErrorException;
import java.util.List;

@Service
@AllArgsConstructor
public class GetBarService {
    private ProjectDAO projectDAO;
    public final List<Bar> updatedTaskList(Long projectId){
        try {
            Project project = projectDAO.getProjectById(projectId);
            if (project != null) {
                return project.getBars();
            }
            throw new NotFoundException("Project was not found.");
        } catch (Exception e) {
            System.out.println("Error while fetching bars: " + e.getMessage());
            throw new InternalServerErrorException();
        }
    }
}
