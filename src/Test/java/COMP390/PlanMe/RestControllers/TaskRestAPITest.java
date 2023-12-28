package COMP390.PlanMe.RestControllers;

import COMP390.PlanMe.RestControllers.Task.DeleteTaskMethodRestApi;
import COMP390.PlanMe.RestControllers.Task.GetTaskMethodRestApi;
import COMP390.PlanMe.RestControllers.Task.PatchTaskMethodRestApi;
import COMP390.PlanMe.RestControllers.Task.PostTaskMethodRestApi;
import COMP390.PlanMe.Services.NotificationService;
import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Entity.Project;
import COMP390.PlanMe.Entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class TaskRestAPITest {
    @Mock
    private ProjectDAO projectDAO;

    @Mock
    private BarDAO barDAO;
    @Mock
    private TaskDAO taskDAO;


    @Mock
    private NotificationService notificationService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
     void testAddTask() {
        // Mock dependencies
        ProjectDAO projectDAO = mock(ProjectDAO.class);
        BarDAO barDAO = mock(BarDAO.class);
        TaskDAO taskDAO = mock(TaskDAO.class);

        PostTaskMethodRestApi taskController = new PostTaskMethodRestApi(projectDAO, taskDAO, barDAO, notificationService);

        // Test data
        Long projectId = 1L;
        String taskName = "Test Task";
        int priority = 1;
        Long barId = 1L;

        // Mock the project and bar
        Project mockProject = new Project();
        Bar mockBar = new Bar();
        mockBar.setId(barId);
        mockBar.setTasks(new ArrayList<>());
        mockProject.setBars(Collections.singletonList(mockBar));

        // Mock DAO methods
        when(projectDAO.getProjectById(projectId)).thenReturn(mockProject);
        when(barDAO.save(any(Bar.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(taskDAO.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute the method
        ResponseEntity<String> responseEntity = taskController.addTask(projectId, taskName, priority, barId);

        // Verify the result
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(taskName, responseEntity.getBody());

        // Verify that the task was added to the bar
        assertEquals(1, mockBar.getTasks().size());
        Task addedTask = mockBar.getTasks().get(0);
        assertEquals(taskName, addedTask.getName());
        assertEquals(mockBar, addedTask.getBar());
        assertEquals(priority, addedTask.getPriority());
        assertEquals(mockBar.getName(), addedTask.getState());
        assertEquals(mockProject, addedTask.getProject());
        assertEquals(1, addedTask.getPosition());

        // Verify DAO method invocations
        verify(projectDAO, times(1)).getProjectById(projectId);
        verify(barDAO, times(1)).save(mockBar);
        verify(taskDAO, times(1)).save(addedTask);
    }
    //Here there was 1 arugmnet neccessary taskId and task must have assigned a name to it
    @Test
    void testNewName() {
        // Mocks
        TaskDAO taskDAO = mock(TaskDAO.class);
        GetTaskMethodRestApi taskController = new GetTaskMethodRestApi(projectDAO, taskDAO, barDAO, notificationService);

        // Test data
        Long taskId = 1L;
        String expectedTaskName = "ExpectedTaskName";
        Task mockTask = new Task();
        //Assigning data to task
        mockTask.setId(taskId);
        mockTask.setName(expectedTaskName);

        // Mock behavior
        when(taskDAO.getTaskById(taskId)).thenReturn(mockTask);

        // Method under test
        ResponseEntity<String> responseEntity = taskController.newName(taskId);

        // Assertions
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedTaskName, responseEntity.getBody());
        verify(taskDAO, times(1)).getTaskById(taskId);
    }
    @Test
    void testUpdateTaskPosition() {
        // Mocks
        TaskDAO taskDAO = mock(TaskDAO.class);
        BarDAO barDAO = mock(BarDAO.class);
        PatchTaskMethodRestApi taskController = new PatchTaskMethodRestApi(projectDAO, taskDAO, barDAO, notificationService);

        // Test data
        Long taskId = 1L;
        Long newPosition = 2L;
        Long originalBarId = 1L;
        Long newBarId = 2L;

        // Mock behavior
        Task mockTask = new Task(); // Set up a Task object as needed
        Bar originalBar = new Bar(); // Set up a Bar object as needed
        Bar newBar = new Bar(); // Set up a new Bar object as needed

        originalBar.getTasks().add(mockTask);
        mockTask.setBar(originalBar);
        mockTask.setId(taskId);
        originalBar.setId(originalBarId);
        newBar.setId(newBarId);

        when(taskDAO.getTaskById(taskId)).thenReturn(mockTask);
        when(barDAO.getBarById(newBarId)).thenReturn(newBar);

        // Execute the method
        ResponseEntity<Long> responseEntity = taskController.updateTaskPosition(taskId, newPosition, newBarId);

        // Verify the result
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(newPosition, responseEntity.getBody());

        verify(taskDAO, times(1)).getTaskById(taskId);
        verify(barDAO, times(1)).getBarById(newBarId);
        verify(barDAO, times(1)).save(newBar);
        verify(taskDAO, times(1)).save(mockTask);
    }
    @Test
    void testUpdateTaskName(){
        // Mocks
        TaskDAO taskDAO = mock(TaskDAO.class);
        PatchTaskMethodRestApi taskController = new PatchTaskMethodRestApi(projectDAO, taskDAO, barDAO, notificationService);

        //Test data
        Task mockTask = new Task();
        Long taskId = 1L;
        String taskName = "New task name";
        //Assign data
        mockTask.setId(taskId);
        //Mock behaviour
        when(taskDAO.getTaskById(taskId)).thenReturn(mockTask);
        //Execute method
        ResponseEntity<Void> responseEntity = taskController.updateTaskName(taskId, taskName);
        //Check status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    //Task Priority can only be positive int between 1 and 3
    @Test
    void testUpdateTaskPriority(){
        // Mocks
        TaskDAO taskDAO = mock(TaskDAO.class);
        PatchTaskMethodRestApi taskController = new PatchTaskMethodRestApi(projectDAO, taskDAO, barDAO, notificationService);

        //Test data
        Task mockTask = new Task();
        Long taskId = 1L;
        int taskPriority = 1;
        //Assign data
        mockTask.setId(taskId);
        mockTask.setPriority(taskPriority);
        //Mock behaviour
        when(taskDAO.getTaskById(taskId)).thenReturn(mockTask);
        //Execute method
        ResponseEntity<Void> responseEntity = taskController.updateTaskPriority(taskId, taskPriority);
        //Check status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void testRemoveTask(){
        // Mocks
        TaskDAO taskDAO = mock(TaskDAO.class);
        DeleteTaskMethodRestApi taskController = new DeleteTaskMethodRestApi(projectDAO, taskDAO, barDAO, notificationService);
        //Mock data
        Task mockTask = new Task();
        Bar bar1 = new Bar();
        Long taskId = 1L;
        mockTask.setBar(bar1);
        mockTask.setId(taskId);

        //Mock behaviour
        when(taskDAO.getTaskById(taskId)).thenReturn(mockTask);
        //Execute method
        ResponseEntity<Void> responseEntity = taskController.removeTask(taskId);
        //Check status
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    void testAddDescription(){
        // Mocks
        TaskDAO taskDAO = mock(TaskDAO.class);
        PostTaskMethodRestApi taskController = new PostTaskMethodRestApi(projectDAO, taskDAO, barDAO, notificationService);
        //Mock data
        Task mockTask = new Task();
        Long taskId = 1L;
        String taskDescription = "Task description";
        mockTask.setId(taskId);
        //Mock behaviour
        when(taskDAO.getTaskById(taskId)).thenReturn(mockTask);
        ResponseEntity<Void> responseEntity = taskController.addDescription(taskId, taskDescription);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    void testSearchTask(){
        // Mocks
        ProjectDAO projectDAO = mock(ProjectDAO.class);
        GetTaskMethodRestApi taskController = new GetTaskMethodRestApi(projectDAO, taskDAO, barDAO, notificationService);
        //Mock data
        Project mockProject = new Project();
        Task mockTask = new Task();
        Map<Long, String> expectedMap = new HashMap<>();

        String taskName = "task name";
        Long projectId = 1L;
        String searchTaskName = "task name"; // value to find
        List<Task> tasks = new ArrayList<>();
        tasks.add(mockTask);
        mockProject.setTasks(tasks);
        mockProject.setId(projectId);
        mockTask.setName(taskName);

        expectedMap.put(mockTask.getId(), "Task found");//return map with task ID and Name

        //Mock behaviour
        when(projectDAO.getProjectById(projectId)).thenReturn(mockProject);
        ResponseEntity<Map<Long, String>> responseEntity = taskController.searchTask(projectId, searchTaskName);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedMap, responseEntity.getBody());
    }
//    @Test
//    void testGetTaskName(){
//        // Mocks
//        TaskDAO taskDAO = mock(TaskDAO.class);
//        TaskRestAPI taskController = new TaskRestAPI(projectDAO, userDAO, taskDAO, barDAO, notificationService);
//
//        Task mockTask = new Task();
//        Long taskId = 1L;
//        mockTask.setId(taskId);
//
//
//        when(taskDAO.findById(taskId)).thenReturn(mockTask);
//
//        ResponseEntity<String> responseEntity = taskController.getTaskName(taskId);
//        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//
//    }
}