package COMP390.PlanMe.RestControllers.Member;

import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Dao.UserDAO;
import COMP390.PlanMe.Entity.Project;
import COMP390.PlanMe.Entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostMemberMethodRestApi {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;

    public PostMemberMethodRestApi(ProjectDAO projectDAO, UserDAO userDAO, TaskDAO taskDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
    }
    @PostMapping("/project/addMember")
    public ResponseEntity<Void> addMember(@RequestParam("projectId") Long projectId, @RequestParam("memberEmail") String memberEmail) {
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            User member = userDAO.findByEmail(memberEmail);
            if (member != null) {
                project.getMembers().add(member);
                projectDAO.save(project);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}