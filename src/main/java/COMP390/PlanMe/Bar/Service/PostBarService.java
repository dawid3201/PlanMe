package COMP390.PlanMe.Bar.Service;

import COMP390.PlanMe.Bar.Bar;
import COMP390.PlanMe.Bar.BarDAO;
import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Exceptions.ConflictException;
import COMP390.PlanMe.Exceptions.NotFoundException;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
@Service
@AllArgsConstructor
public class PostBarService {
    private final ProjectDAO projectDAO;
    private final BarDAO barDAO;

    public final Bar addBar(Long projectId, String barName) throws IllegalArgumentException, NotFoundException{
        if(barName.isEmpty()){
            throw new IllegalArgumentException("Bar name cannot be empty.");
        }
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

            return newBar;
        }
        throw new NotFoundException("The Project was not found");
    }
}
