package COMP390.PlanMe.RestControllers;

import COMP390.PlanMe.dao.ProjectDAO;
import COMP390.PlanMe.dao.TaskDAO;
import COMP390.PlanMe.dao.UserDAO;
import COMP390.PlanMe.dao.barDAO;
import COMP390.PlanMe.entity.Bar;
import COMP390.PlanMe.entity.Project;
import COMP390.PlanMe.entity.Task;
import COMP390.PlanMe.entity.User;
import jakarta.persistence.EntityNotFoundException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
public class RestControllerProject {

    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;
    private final barDAO barDAO;

    @Autowired
    public RestControllerProject(ProjectDAO projectDAO, UserDAO userDAO, TaskDAO taskDAO, COMP390.PlanMe.dao.barDAO barDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
        this.barDAO = barDAO;
    }

    //-----------------------------------------------------------------BAR METHODS--------------------------------------
    @Transactional
    @PatchMapping("/project/updateBarPosition")
    public ResponseEntity<Map<String, Long>> updateBarPosition(@RequestParam("barId") Long barId,
                                                               @RequestParam("newPosition") Long newPosition) {
        try {
            Bar bar = barDAO.getBarById(barId);
            Project project = bar.getProject();
            List<Bar> bars = barDAO.findByProjectId(project.getId());
            bars.sort(Comparator.comparing(Bar::getPosition));
            bars.remove(bar);
            bars.add(newPosition.intValue() - 1, bar);
            reorderBarPositions(bars);

            barDAO.saveAll(bars);
            projectDAO.save(project);
            return ResponseEntity.ok(Map.of("barId", barId, "newPosition", newPosition));

        } catch (Exception e) {
            System.out.println("Error while updating bar position: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    private void reorderBarPositions(List<Bar> bars) {
        for (int i = 0; i < bars.size(); i++) {
            bars.get(i).setPosition(i + 1);
        }
    }
    @GetMapping("/project/addBar")
    public ResponseEntity<Bar> addBar(@RequestParam("projectId") Long projectId, @RequestParam("barName") String barName) {
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            Bar newBar = new Bar();
            newBar.setName(barName);
            Integer maxPosition = barDAO.getMaxPositionByProject(project);
            newBar.setPosition(maxPosition != null ? maxPosition + 1 : 1);
            newBar.setProject(project);
            barDAO.save(newBar);
            project.getBars().add(newBar);
            projectDAO.save(project);

            return ResponseEntity.ok(newBar);
        }
        return ResponseEntity.notFound().build();
    }
    @PatchMapping("/project/updateBarName")
    public ResponseEntity<Bar> updateBarName(@RequestParam("barId") Long barId, @RequestParam("barName") String barName) {
        Bar bar = barDAO.getBarById(barId);
        if (bar != null) {
            String oldBarName = bar.getName();
            bar.setName(barName);
            List<Task> tasks = taskDAO.findAllByState(oldBarName);
            for (Task task : tasks) {
                task.setState(barName);
                taskDAO.save(task);
            }
            barDAO.save(bar);
            return ResponseEntity.ok(bar);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/project/deleteBar")
    public ResponseEntity<Void> deleteBar(@RequestParam("barId") Long barId) {
        Bar bar = barDAO.getBarById(barId);
        if (bar != null) {
            List<Bar> barsToUpdate = barDAO.getBarsByProjectAndPositionGreaterThan(bar.getProject(), bar.getPosition());
            for(Bar barToUpdate : barsToUpdate){
                barToUpdate.setPosition(barToUpdate.getPosition()-1);
                barDAO.save(barToUpdate);
            }
            barDAO.delete(bar);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    //---------------------------------------------------MEMBERS METHODS------------------------------------------------
    @PostMapping("/project/addMember")
    public ResponseEntity<Void> addMember(@RequestParam("projectId") Long projectId, @RequestParam("memberEmail") String memberEmail) {
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            User member = userDAO.getUserByEmail(memberEmail);
            if (member != null) {
                project.getMembers().add(member);
                projectDAO.save(project);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    //----------------------------------------------TASK METHODS--------------------------------------------------------
    @PostMapping("/project/addTask")
    public ResponseEntity<Void> addTask(
            @RequestParam("projectId") Long projectId,
            @RequestParam("taskName") String taskName,
            @RequestParam("taskPriority") int priority,
            @RequestParam("barId") Long barId) {
        //Adding task to the bar with specific position
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            Bar targetBar = project.getBars().stream()
                    .filter(bar -> bar.getId().equals(barId)).findFirst().orElse(null);
            if (targetBar == null) {
                return ResponseEntity.notFound().build();
            }
            Task newTask = new Task();
            newTask.setName(taskName);
            newTask.setBar(targetBar);
            newTask.setPriority(priority);
            newTask.setState(targetBar.getName());
            newTask.setProject(project);
            newTask.setPosition((long) (targetBar.getTasks().size() + 1));
            targetBar.getTasks().add(newTask);
            taskDAO.save(newTask);
            barDAO.save(targetBar);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PatchMapping("/project/updateTaskName")
    public ResponseEntity<Void> updateTaskName(@RequestParam("taskId") Long taskId, @RequestParam("taskName") String taskName){
        try {
            Task task = taskDAO.getOne(taskId);
            task.setName(taskName);
            taskDAO.save(task);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @Transactional
    @PatchMapping("/project/updateTaskPosition")
    public ResponseEntity<Void> updateTaskPosition(@RequestParam("taskId") Long taskId,
                                                   @RequestParam("newPosition") Long newPosition,
                                                   @RequestParam("barId") Long barId) {
        try {
            Task task = taskDAO.getTaskById(taskId);
            Bar originalBar = task.getBar();
            if(originalBar != null) {
                List<Task> originalBarTasks = originalBar.getTasks();
                originalBarTasks.remove(task);
                reorderTaskPositions(originalBarTasks);
                originalBar.setTasks(originalBarTasks);
                barDAO.save(originalBar);
            }
            Bar newBar = barDAO.getBarById(barId);
            List<Task> newBarTasks = newBar.getTasks();

            if (newBarTasks.size() <= newPosition.intValue() - 1) {
                newBarTasks.add(task);
            } else {
                newBarTasks.add(newPosition.intValue() - 1, task);
            }
            reorderTaskPositions(newBarTasks);
            newBar.setTasks(newBarTasks);
            task.setBar(newBar);
            task.setState(newBar.getName());
            taskDAO.save(task);
            barDAO.save(newBar);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Error while updating task position: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private void reorderTaskPositions(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setPosition((long) (i + 1));
        }
    }
    @PatchMapping("/project/updateTaskPriority")
    public ResponseEntity<Void> updateTaskPrio(@RequestParam ("taskId") Long taskId, @RequestParam ("newTaskPriority") int newPriority) {
        try{
            Task task = taskDAO.getOne(taskId);
            task.setPriority(newPriority);
            taskDAO.save(task);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/project/removeTask")
    public ResponseEntity<Void> removeTask(@RequestParam("taskId") Long taskId) {
        try {
            Task task = taskDAO.getOne(taskId);
            Bar bar = task.getBar();
            bar.getTasks().remove(task);
            barDAO.save(bar);
            taskDAO.delete(task);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    //Adding new description to a task
    @PostMapping("/project/addDescription")
    public ResponseEntity<Void> addDescription(@RequestParam("taskId")Long taskId, @RequestParam("description") String description){
        try{
            Task task = taskDAO.getTaskById(taskId);
            task.setDescription(description);
            taskDAO.save(task);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}