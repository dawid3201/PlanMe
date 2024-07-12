package COMP390.PlanMe.RestControllers;

import COMP390.PlanMe.Bar.Bar;
import COMP390.PlanMe.Exceptions.TaskNotFoundException;
import COMP390.PlanMe.Exceptions.UserAlreadyAssignedException;
import COMP390.PlanMe.Exceptions.UserNotFoundException;
import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.Task.TaskDAO;
import COMP390.PlanMe.User.UserDAO;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Task.Task;
import COMP390.PlanMe.User.User;
import COMP390.PlanMe.User.ProjectMember.MemberRestController;
import COMP390.PlanMe.User.ProjectMember.Service.GetMemberService;
import COMP390.PlanMe.User.ProjectMember.Service.PatchMemberService;
import COMP390.PlanMe.User.ProjectMember.Service.PostMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        user.getTasksAssigned().add(task); // add task to user list of assigned tasks

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

        System.out.println("user.getTasksAssigned(): " + user.getTasksAssigned());
        System.out.println("task: " + task);
    }
    @Test
    public void assignUser_Invalid(){ //Task already assigned

        //Mock Behaviour
        when(taskDAO.getTaskById(1L)).thenReturn(task);//find task by ID
        when(userDAO.findByEmail("member@email.com")).thenReturn(user);//find user by emial
        //Expect excpetion as we trying to assign task that is already assgined to this user
        assertThrows(UserAlreadyAssignedException.class, () -> memberRestController.assignUser(user.getEmail(), 1L));
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
