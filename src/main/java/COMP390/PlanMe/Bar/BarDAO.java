package COMP390.PlanMe.Bar;

import COMP390.PlanMe.Project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BarDAO extends JpaRepository<Bar, Long> {
    Bar getBarById(Long Id);
    @Query("SELECT b FROM Bar b WHERE b.project = :project AND b.position > :position")
    List<Bar> getBarsByProjectAndPositionGreaterThan(Project project, int position);

    @Query("SELECT max(b.position) FROM Bar b WHERE b.project = :project")
    Integer getMaxPositionByProject(Project project);

    List<Bar> findByProjectId(Long id);
}
