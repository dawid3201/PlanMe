package COMP390.PlanMe.User.ProjectMember.Service;

import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.User.UserDAO;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.User.User;
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
