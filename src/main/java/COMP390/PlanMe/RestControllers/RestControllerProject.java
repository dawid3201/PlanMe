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
import org.springframework.web.bind.annotation.*;

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


    //----------------------------------------------BAR METHODS----------------------------------------------
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

    //---------------------------------------------------MEMBERS METHODS-----------------------------------------
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


    //----------------------------------------------TASK METHODS----------------------------------------------
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
    @GetMapping("/project/getTasks")
    public ResponseEntity<List<Task>> getTasks(@RequestParam("projectId") Long projectId) {
        Project project = projectDAO.getProjectById(projectId);
        if (project != null) {
            List<Task> tasks = project.getTasks();  // Fetches the tasks for the project
            return ResponseEntity.ok(tasks);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PatchMapping("/project/updateTaskSwimlane")
    public ResponseEntity<Void> updateTaskSwimlane(@RequestParam("taskId") Long taskId, @RequestParam("newSwimlane") String newSwimlane, @RequestParam("barId") Long barId) {
        Task task = taskDAO.getOne(taskId);
        Bar bar = barDAO.getBarById(barId);
        task.setBar(bar);
        task.setState(bar.getName());
        taskDAO.save(task);
        return ResponseEntity.ok().build();
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

    @PatchMapping("/project/updateTaskPosition")
    public ResponseEntity<Void> updateTaskPosition(@RequestParam("taskId") Long taskId, @RequestParam("newPosition") Long newPosition) {
        try {
            Task task = taskDAO.getOne(taskId);
            Bar bar = task.getBar();
            List<Task> tasks = bar.getTasks();
            tasks.remove(task);
            tasks.add(newPosition.intValue() - 1, task);
            for (int i = 0; i < tasks.size(); i++) {
                tasks.get(i).setPosition((long) (i + 1));
            }
            bar.setTasks(tasks);
            barDAO.save(bar);
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
