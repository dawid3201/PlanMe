package COMP390.PlanMe.Project.Controller;

import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Exceptions.BadArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PatchProjectController {
    private final ProjectDAO projectDAO;

    @Autowired
    public PatchProjectController(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }
    @PatchMapping("/project/updateProjectName")
    public final ResponseEntity<Void> updateProjectName(@RequestParam("projectId") Long projectId, @RequestParam("newName") String name){
        Project project = projectDAO.getProjectById(projectId);
        if(project != null){
            if(!name.isEmpty() && name.length() <= 25){
                project.setName(name);
                projectDAO.save(project);
            }
            else{
                throw new BadArgumentException("Project name cannot be empty and not longer than 25 characters.");
            }
        }
        return ResponseEntity.ok().build();
    }
}