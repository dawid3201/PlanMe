package COMP390.PlanMe.Task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskDAO extends JpaRepository<Task, Long> {
    List<Task> findAllByState(String oldBarName);
    Task getTaskById(Long taskId);
}

