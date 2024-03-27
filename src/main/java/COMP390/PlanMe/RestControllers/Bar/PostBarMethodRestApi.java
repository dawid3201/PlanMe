package COMP390.PlanMe.RestControllers.Bar;

import COMP390.PlanMe.Exceptions.ConflictException;
import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Entity.Project;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostBarMethodRestApi {
    private final ProjectDAO projectDAO;
    private final BarDAO barDAO;

    public PostBarMethodRestApi(ProjectDAO projectDAO, BarDAO barDAO) {
        this.projectDAO = projectDAO;
        this.barDAO = barDAO;
    }
    @PostMapping("/project/addBar")
    public final ResponseEntity<Bar> addBar(@RequestParam("projectId") Long projectId, @RequestParam("barName") String barName){
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
}
