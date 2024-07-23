package COMP390.PlanMe.ProjectMember.Service;

import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.User.UserDAO;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.User.User;
import COMP390.PlanMe.Exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GetMemberService {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;

    public String getTaskName(Long projectId) {
        Project project = projectDAO.getProjectById(projectId);
        if(project != null){
            List<String> userNames = new ArrayList<>();
            for(User user : project.getMembers()){
                userNames.add(user.getFirstName() + " " + user.getLastName() + " | " + user.getEmail());
            }
            return String.join("\n", userNames);
        }
        throw new NotFoundException("Member email was not found.");
    }
    public String getInitials(String email){
        User user = userDAO.findByEmail(email);
        if(user != null){
            String fullName = user.getFirstName() + " " + user.getLastName();
            String[] nameParts = fullName.split("\\s");
            StringBuilder initials  = new StringBuilder();
            for(String namePart : nameParts){
                initials.append(namePart.charAt(0));
            }
            return initials.toString().toUpperCase();
        }
        throw new NotFoundException("User with email: " + email + " does not exist.");
    }
}
