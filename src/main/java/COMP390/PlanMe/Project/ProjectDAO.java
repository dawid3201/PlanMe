package COMP390.PlanMe.Project;

import COMP390.PlanMe.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProjectDAO extends JpaRepository<Project, Long> {

    Project getProjectById(Long id);
    @Query("SELECT p FROM Project p WHERE :user MEMBER OF p.members")
    List<Project> getProjectsByUser(User user);

    @Query("SELECT p FROM Project p JOIN p.bars t WHERE t.id = :barId")
    Project getProjectByBarId(@Param("barId") Long barId);
}
