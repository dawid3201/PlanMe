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


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
@RestController
public class RestControllerProject {

    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;
    private final TaskDAO taskDAO;
    private final barDAO barDAO;


    public RestControllerProject(ProjectDAO projectDAO, UserDAO userDAO, TaskDAO taskDAO, COMP390.PlanMe.dao.barDAO barDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
        this.taskDAO = taskDAO;
        this.barDAO = barDAO;
    }


    //-----------------------------------------------------------------BAR METHODS--------------------------------------
    @Transactional
    @PatchMapping("/project/updateBarPosition")
    public ResponseEntity<Void> updateBarPosition(@RequestParam("barId") Long barId,
                                                  @RequestParam("newPosition") Long newPosition) {
        try {
            Bar bar = barDAO.getBarById(barId);
            Project project = bar.getProject();
            List<Bar> bars = barDAO.findByProjectId(project.getId());

            System.out.println("About to change the position of" + bar.getName() + " at positoin " + bar.getPosition());

            bars.sort(Comparator.comparing(Bar::getPosition));
            bars.remove(bar);
            bars.add(newPosition.intValue() - 1, bar);
            reorderBarPositions(bars);

            barDAO.saveAll(bars);
            projectDAO.save(project);

            System.out.println("New positions of " + bar.getName() + " is " + newPosition);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            System.out.println("Error while updating bar position: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
            //Update all tasks with new state name
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
            List<Bar> barsToUpdate = barDAO.getBarsByProjectAndPositionGreaterThan(bar.getProject(), bar.getPosition()); // get all bars with higher position
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
    public ResponseEntity<Void> addTask(@RequestParam("projectId") Long projectId, @RequestParam("taskDescription") String taskDescription, @RequestParam("taskPriority") int priority) {
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            Bar todoBar = project.getBars().stream().filter(bar -> bar.getPosition() == 1).findFirst().orElse(null);
            if (todoBar == null) {
                return ResponseEntity.notFound().build();
            }
            Task newTask = new Task();
            newTask.setDescription(taskDescription);
            newTask.setBar(todoBar);
            newTask.setPriority(priority);
            newTask.setState(todoBar.getName());
            newTask.setProject(project);
            newTask.setPosition((long) (todoBar.getTasks().size() + 1));
            todoBar.getTasks().add(newTask);
            taskDAO.save(newTask);
            barDAO.save(todoBar);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/project/updateTaskName")
    public ResponseEntity<Void> updateTaskName(@RequestParam("taskId") Long taskId, @RequestParam("taskDescription") String taskDescription){
        try {
            Task task = taskDAO.getOne(taskId);
            task.setDescription(taskDescription);
            taskDAO.save(task);
            return ResponseEntity.ok().build(); // Return a 200 OK response
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // Return a 404 Not Found response if the task doesn't exist
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
            Task task = taskDAO.getOne(taskId);
            Bar originalBar = task.getBar();
            System.out.println("attempting to move tasks with name " + task.getDescription() + " with position " + task.getPosition());
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
            System.out.println("Moved task " + task.getDescription() + " to new position: " + task.getPosition());
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
}