package COMP390.PlanMe.RestControllers.Bar;

import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Entity.Project;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GetBarMethodRestApi {
    private final ProjectDAO projectDAO;

    public GetBarMethodRestApi(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }
    @GetMapping("/project/getUpdatedBars/{projectId}")
    public final ResponseEntity<List<Bar>> updatedTaskList(@PathVariable("projectId") Long projectId){
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
}