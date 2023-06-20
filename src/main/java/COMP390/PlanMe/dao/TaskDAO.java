package COMP390.PlanMe.dao;

import COMP390.PlanMe.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskDAO extends JpaRepository<Task, Long> {

}

