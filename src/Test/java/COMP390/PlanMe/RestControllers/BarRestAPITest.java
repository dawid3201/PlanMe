package COMP390.PlanMe.RestControllers;

import COMP390.PlanMe.Bar.BarDAO;
import COMP390.PlanMe.Bar.BarRestController;
import COMP390.PlanMe.Exceptions.NotFoundException;
import COMP390.PlanMe.Project.ProjectDAO;
import COMP390.PlanMe.Bar.Bar;
import COMP390.PlanMe.Project.Project;
import COMP390.PlanMe.Task.Task;
import COMP390.PlanMe.Bar.Service.DeleteBarService;
import COMP390.PlanMe.Bar.Service.GetBarService;
import COMP390.PlanMe.Bar.Service.PatchBarService;
import COMP390.PlanMe.Bar.Service.PostBarService;
import COMP390.PlanMe.Task.TaskDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class BarRestAPITest {
    @Mock
    private BarDAO barDAO;
    @Mock
    private TaskDAO taskDAO;
    @Mock
    private ProjectDAO projectDAO;
    @InjectMocks
    private DeleteBarService deleteBarService;
    @InjectMocks
    private GetBarService getBarService;
    @InjectMocks
    private PatchBarService patchBarService;
    @InjectMocks
    private PostBarService postBarService;
    @InjectMocks
    private BarRestController barRestController;
    private Bar bar;
    private Project project;
    private Task task;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        barRestController = new BarRestController(deleteBarService, getBarService, patchBarService, postBarService);

        project = new Project();
        project.setId(1L);

        bar = new Bar();
        bar.setId(1L);
        bar.setName("TODO");
        bar.setPosition(1);

        task = new Task();
    }
    @Test
    public void DeleteBarTestOk(){
        when(barDAO.getBarById(1L)).thenReturn(bar);
        ResponseEntity<String> responseEntity = barRestController.deleteBar(1L);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }
    @Test
    public void DeleteBarTestBad(){
        when(barDAO.getBarById(2L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> barRestController.deleteBar(2L));
    }
    @Test
    public void testAddBar_Success(){
        //Mock behaviour
        when(projectDAO.getProjectById(1L)).thenReturn(project);

        //Execute method
        ResponseEntity<Bar> responseEntity = barRestController.addBar(1L, "TODO");
        //Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testAddBar_NotFound(){
        when(projectDAO.getProjectById(anyLong())).thenReturn(null);

        assertThrows(NotFoundException.class, () -> barRestController.addBar(1L, "validBarName"));
    }
    @Test
    public void testAddBar_IllegalArgument(){
        when(projectDAO.getProjectById(anyLong())).thenReturn(project);

        assertThrows(IllegalArgumentException.class, () -> barRestController.addBar(1L, ""));
    }

    //Patch
    @Test
    public void testUpdateBarPosition_Success() {
        bar.setProject(project);
        when(barDAO.getBarById(1L)).thenReturn(bar);
        when(projectDAO.getProjectById(project.getId())).thenReturn(project);
        // Execute method
        ResponseEntity<Map<String, Long>> responseEntity = barRestController.updateBarPosition(bar.getId(), 2L);
        //Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testUpdateBarPosition_Invalid() {
        // Mock behavior
        when(barDAO.getBarById(2L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> barRestController.updateBarPosition(1L, 2L));
    }
    @Test
    public void testUpdateBarName_Success(){
        //Mock data
        String newName = "New bar name";
        //Mock behaviour
        when(barDAO.getBarById(1L)).thenReturn(bar);
        when(taskDAO.findAllByState(bar.getName())).thenReturn(bar.getTasks());
        //Execute method
        ResponseEntity<Bar> responseEntity = barRestController.updateBarName(1L, newName);
        //Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testUpdateBarName_NotFound(){
        //Mock data
        String newName = "New bar name";
        //Mock behaviour
        when(barDAO.getBarById(1L)).thenReturn(null);
        //Execute method
        //Verify
        assertThrows(NotFoundException.class, () -> barRestController.updateBarName(bar.getId(), newName));
    }
    //GET
    @Test
    public void testUpdateTaskList_Success(){
        //Mock data
        List<Bar> barList = new ArrayList<>(List.of(bar));
        project.setBars(barList);
        //Mock behaviour
        when(projectDAO.getProjectById(1L)).thenReturn(project);
        //Execute method
        ResponseEntity<List<Bar>> responseEntity = barRestController.updateTaskList(1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testUpdateTaskList_NotFound(){
        //Mock data
        List<Bar> barList = new ArrayList<>(List.of(bar));
        project.setBars(barList);
        //Mock behaviour
        when(projectDAO.getProjectById(2L)).thenReturn(null);
        //Execute method
        assertThrows(NotFoundException.class, () -> barRestController.updateTaskList(2L));
    }


}
