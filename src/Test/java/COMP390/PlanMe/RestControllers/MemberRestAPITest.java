package COMP390.PlanMe.RestControllers;

import COMP390.PlanMe.Bar.Bar;
import COMP390.PlanMe.Exceptions.ProjectNotFoundException;
import COMP390.PlanMe.Exceptions.TaskNotFoundException;
import COMP390.PlanMe.Exceptions.UserAlreadyAssignedException;
import COMP390.PlanMe.Exceptions.UserNotFoundException;
import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.Task.TaskDAO;
import COMP390.PlanMe.User.UserDAO;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Task.Task;
import COMP390.PlanMe.User.User;
import COMP390.PlanMe.ProjectMember.MemberRestController;
import COMP390.PlanMe.ProjectMember.Service.GetMemberService;
import COMP390.PlanMe.ProjectMember.Service.PatchMemberService;
import COMP390.PlanMe.ProjectMember.Service.PostMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
public class MemberRestAPITest {
    @Mock
    private ProjectDAO projectDAO;

    @Mock
    private UserDAO userDAO;

    @Mock
    private TaskDAO taskDAO;

    @InjectMocks
    private MemberRestController memberRestController;
    @InjectMocks
    private GetMemberService getMemberService;
    @InjectMocks
    private PostMemberService postMemberService;
    @InjectMocks
    private PatchMemberService patchMemberService;
    private Task task;
    private Project project;
    private Bar bar;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        memberRestController = new MemberRestController(getMemberService, postMemberService, patchMemberService);

        task = new Task();
        task.setId(1L);
        task.setName("taskName");

        project = new Project();
        project.setId(1L);

        bar = new Bar();

        user = new User();
        user.setEmail("member@email.com");

    }
    @Test
    public void assignUser_Success() throws UserNotFoundException, UserAlreadyAssignedException, TaskNotFoundException {
        //Mock Behaviour
        when(taskDAO.getTaskById(1L)).thenReturn(task);
        when(userDAO.findByEmail("member@email.com")).thenReturn(user);
        //Method
        ResponseEntity<String> response = memberRestController.assignUser("member@email.com", 1L);
        //Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    @Test
    public void assignUser_UserAlreadyAssignedException(){
        user.getTasksAssigned().add(task);//potential issue, not correcly assgined task to user
        task.setAssignedUser(user);
        when(taskDAO.getTaskById(1L)).thenReturn(task);
        when(userDAO.findByEmail("member@email.com")).thenReturn(user);


        assertThrows(UserAlreadyAssignedException.class, () ->
                memberRestController.assignUser("member@email.com", 1L));
    }
    @Test
    public void assignUser_TaskNotFoundException(){

        when(taskDAO.getTaskById(2L)).thenReturn(null);
        when(userDAO.findByEmail("member@email.com")).thenReturn(user);


        assertThrows(TaskNotFoundException.class, () ->
                memberRestController.assignUser("member@email.com", 2L));
    }
    @Test
    public void assignUser_UserNotFoundException(){

        when(taskDAO.getTaskById(1L)).thenReturn(task);
        when(userDAO.findByEmail("test@email.com")).thenReturn(null);


        assertThrows(UserNotFoundException.class, () ->
                memberRestController.assignUser("test@email.com", 1L));
    }
    @Test
    public void testAddMember_Success() throws UserNotFoundException, ProjectNotFoundException {
        // Mock behavior
        when(projectDAO.getProjectById(anyLong())).thenReturn(project);
        when(userDAO.findByEmail(anyString())).thenReturn(user);

        ResponseEntity<Void> response = memberRestController.addMember(1L, "member@email.com");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddMember_ProjectNotFound() {

        // Mock behavior
        when(projectDAO.getProjectById(1L)).thenReturn(null);

        assertThrows(ProjectNotFoundException.class, () ->
                memberRestController.addMember(1L, "test@email.com"));

    }
    @Test
    public void testAddMember_UserNotFound(){
        // Mock behavior
        when(projectDAO.getProjectById(1L)).thenReturn(project);
        when(userDAO.findByEmail("incorrect@emial.com")).thenReturn(null);

        assertThrows(UserNotFoundException.class, () ->
                memberRestController.addMember(1L, "incorrect@emial.com"));
    }
    //Repo test for User
    @Test
    void testFindByEmail(){
        userDAO.save(user);

        when(userDAO.findByEmail("member@email.com")).thenReturn(user);

        assertEquals("member@email.com", user.getEmail());
    }
}
