package COMP390.PlanMe.dao;

import COMP390.PlanMe.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskDAO extends JpaRepository<Task, Long> {

    List<Task> findAllByState(String oldBarName);

    Task getTaskById(Long taskId);
}

