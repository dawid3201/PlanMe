package COMP390.PlanMe.dao;

import COMP390.PlanMe.entity.Project;
import COMP390.PlanMe.entity.User;

import java.util.List;

public interface ProjectDAO {
    void save(Project project);
    Project getProjectById(Long id);
    List<Project> getProjectsByUser(User user);

}
