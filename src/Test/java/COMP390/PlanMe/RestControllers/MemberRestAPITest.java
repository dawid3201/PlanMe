package COMP390.PlanMe.RestControllers;

import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Dao.UserDAO;
import COMP390.PlanMe.Entity.Project;
import COMP390.PlanMe.Entity.Task;
import COMP390.PlanMe.Entity.User;
import COMP390.PlanMe.RestControllers.Member.MemberRestController;
import COMP390.PlanMe.Services.Member.GetMemberService;
import COMP390.PlanMe.Services.Member.PatchMemberService;
import COMP390.PlanMe.Services.Member.PostMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
@SpringBootTest
public class MemberRestAPITest {
    @Mock
    private ProjectDAO projectDAO;

    @Mock
    private UserDAO userDAO;

    @Mock
    private TaskDAO taskDAO;


    private MemberRestController memberRestController;
    @Mock
    private GetMemberService getMemberService;
    @Mock
    private PostMemberService postMemberService;
    @Mock
    private PatchMemberService patchMemberService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        memberRestController = new MemberRestController(getMemberService, postMemberService, patchMemberService);

    }
    @Test
    public void assignUser_Success(){
        //Mock Data
        String userEmail = "member@email.com";
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        User user = new User();
        user.setEmail(userEmail);
        Project project = new Project();
        Task task2 = new Task();
        List<Task> taskList = new ArrayList<>(List.of(task2));
        project.setTasks(taskList);

        user.setTasksAssigned(taskList);
        //Mock Behaviour
        when(taskDAO.getTaskById(1L)).thenReturn(task);
        when(userDAO.findByEmail("member@email.com")).thenReturn(user);
        //Method
        ResponseEntity<Boolean> response = memberRestController.assignUser(userEmail, taskId);
        //Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    public void assignUser_Invalid(){ //Task already assigned
        //Mock Data
        String userEmail = "member@email.com";
        Long taskId = 1L;

        Task task = new Task();
        task.setId(taskId);

        User user = new User();
        user.setEmail(userEmail);

        Project project = new Project();
        List<Task> taskList = new ArrayList<>(List.of(task));
        project.setTasks(taskList);

        user.setTasksAssigned(taskList);


        //Mock Behaviour
        when(taskDAO.getTaskById(1L)).thenReturn(task);
        when(userDAO.findByEmail("member@email.com")).thenReturn(user);

        //Verify
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> memberRestController.assignUser(userEmail, taskId));
        assertEquals("User " + userEmail + " is already assign to the task null", exception.getMessage());
    }
    @Test
    public void testAddMember_Success() {
        // Mock data
        Long projectId = 1L;
        String memberEmail = "member@example.com";
        Project project = new Project();
        project.setId(projectId);
        User member = new User();
        member.setEmail(memberEmail);

        // Mock behavior
        when(projectDAO.getProjectById(projectId)).thenReturn(project);
        when(userDAO.findByEmail(memberEmail)).thenReturn(member);

        // Invoke method
        ResponseEntity<Void> response = memberRestController.addMember(projectId, memberEmail);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(projectDAO, times(1)).save(project);
    }

    @Test
    public void testAddMember_ProjectNotFound() {
        // Mock data
        Long projectId = 1L;
        String memberEmail = "member@example.com";

        // Mock behavior
        when(projectDAO.getProjectById(projectId)).thenReturn(null);

        // Invoke method
        ResponseEntity<Void> response = memberRestController.addMember(projectId, memberEmail);

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(projectDAO, never()).save(any());
    }
    @Test
    public void testAddMember_MemberNotFound() {
        // Mock data
        Long projectId = 1L;
        String memberEmail = "member@example.com";
        Project project = new Project();
        project.setId(projectId);

        // Mock behavior
        when(projectDAO.getProjectById(projectId)).thenReturn(project);
        when(userDAO.findByEmail(memberEmail)).thenReturn(null);

        // Invoke method
        ResponseEntity<Void> response = memberRestController.addMember(projectId, memberEmail);

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(projectDAO, never()).save(any());
    }
}
