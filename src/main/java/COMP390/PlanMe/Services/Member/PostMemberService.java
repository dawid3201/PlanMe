package COMP390.PlanMe.Services.Member;

import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.UserDAO;
import COMP390.PlanMe.Entity.Project;
import COMP390.PlanMe.Entity.User;
import COMP390.PlanMe.Exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PostMemberService {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;

    public Void addMember(Long projectId, String memberEmail) {
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            User member = userDAO.findByEmail(memberEmail);
            if (member != null) {
                project.getMembers().add(member);
                projectDAO.save(project);
            }
        }
        throw new NotFoundException("Project was not found.");
    }
}
