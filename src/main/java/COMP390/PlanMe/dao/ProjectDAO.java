package COMP390.PlanMe.dao;

import COMP390.PlanMe.entity.Project;
import COMP390.PlanMe.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProjectDAO extends JpaRepository<Project, Long> {

    Project getProjectById(Long id);
    @Query("SELECT p FROM Project p WHERE :user MEMBER OF p.members")
    List<Project> getProjectsByUser(User user);
}
