package COMP390.PlanMe.Project.Controller;

import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Project.ProjectDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class DeleteProjectController {
    private final ProjectDAO projectDAO;

    @Autowired
    public DeleteProjectController(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }
    //-----------------------------------------------------PROJECT METHODS-----------------------------------------
    @DeleteMapping("/project/deleteProject")
    public final ResponseEntity<Void> deleteBar(@RequestParam("projectId") Long projectId){
        Project project = projectDAO.getProjectById(projectId);
        if(project != null){
            projectDAO.delete(project);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

}