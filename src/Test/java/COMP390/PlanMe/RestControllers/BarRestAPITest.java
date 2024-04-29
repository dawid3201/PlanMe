package COMP390.PlanMe.RestControllers;

import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Entity.Project;
import COMP390.PlanMe.Entity.Task;
import COMP390.PlanMe.RestControllers.Bar.DeleteBarMethodRestApi;
import COMP390.PlanMe.RestControllers.Bar.GetBarMethodRestApi;
import COMP390.PlanMe.RestControllers.Bar.PatchBarMethodRestApi;
import COMP390.PlanMe.RestControllers.Bar.PostBarMethodRestApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BarRestAPITest {
    @Mock
    private BarDAO barDAO;
    @Mock
    private ProjectDAO projectDAO;
    @Mock
    private TaskDAO taskDAO;

    private DeleteBarMethodRestApi deleteBarMethodRestApi;
    private PatchBarMethodRestApi patchBarMethodRestApi;
    private GetBarMethodRestApi getBarMethodRestApi;
    private PostBarMethodRestApi postBarMethodRestApi;
    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        deleteBarMethodRestApi = new DeleteBarMethodRestApi(barDAO);
        patchBarMethodRestApi = new PatchBarMethodRestApi(projectDAO, taskDAO, barDAO);
        getBarMethodRestApi = new GetBarMethodRestApi(projectDAO);
        postBarMethodRestApi = new PostBarMethodRestApi(projectDAO, barDAO);
    }
    @Test
    public void DeleteBarTestOk(){
        //Test data
        Bar bar = new Bar();
        bar.setId(1L);

        when(barDAO.getBarById(1L)).thenReturn(bar);
        ResponseEntity<Void> responseEntity = deleteBarMethodRestApi.deleteBar(1L);
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    }
    @Test
    public void DeleteBarTestBad(){
        //Test data
        Bar bar = new Bar();
        bar.setId(1L);

        when(barDAO.getBarById(2L)).thenReturn(null);
        ResponseEntity<Void> responseEntity = deleteBarMethodRestApi.deleteBar(2L);
        assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
    }
    @Test
    public void testAddBar_Success(){
        //Test Data
        Project project = new Project();
        project.setId(1L);
        Bar bar = new Bar();
        bar.setName("TODO");

        //Mock behaviour
        when(projectDAO.getProjectById(1L)).thenReturn(project);

        //Execute method
        ResponseEntity<Bar> responseEntity = postBarMethodRestApi.addBar(1L, "TODO");
        //Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testAddBar_Invalid(){
        //Test Data
        Project project = new Project();
        project.setId(1L);
        Bar bar = new Bar();
        bar.setName("TODO");

        //Execute method
        ResponseEntity<Bar> responseEntity = postBarMethodRestApi.addBar(2L, "TODO");
        //Verify
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
    //Patch
    @Test
    public void testUpdateBarPosition_Success() {
        // Mock data
        Long barId = 1L;
        Long newPosition = 2L;
        Bar bar = new Bar();
        bar.setId(barId);
        bar.setPosition(1);
        Project project = new Project();
        project.setId(1L);
        bar.setProject(project);
        // Mock behavior
        when(barDAO.getBarById(1L)).thenReturn(bar);
        // Execute method
        ResponseEntity<Map<String, Long>> responseEntity = patchBarMethodRestApi.updateBarPosition(barId, newPosition);
        //Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testUpdateBarPosition_Invalid() {
        // Mock data
        Long barId = 1L;
        Long newPosition = 2L;
        Bar bar = new Bar();
        bar.setId(barId);
        bar.setPosition(1);
        Project project = new Project();
        project.setId(1L);
        bar.setProject(project);
        // Mock behavior
        when(barDAO.getBarById(2L)).thenReturn(null);
        // Execute method
        ResponseEntity<Map<String, Long>> responseEntity = patchBarMethodRestApi.updateBarPosition(2L, newPosition);
        //Verify
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
    @Test
    public void testUpdateBarName_Success(){
        //Mock data
        Bar bar = new Bar();
        bar.setId(1L);
        bar.setName("old name");
        String newName = "New bar name";
        Task task = new Task();
        task.setId(1L);
        task.setBar(bar);
        //Mock behaviour
        when(barDAO.getBarById(1L)).thenReturn(bar);
        //Execute method
        ResponseEntity<Bar> responseEntity = patchBarMethodRestApi.updateBarName(1L, newName);
        //Verify
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    //GET
    @Test
    public void testUpdateTaskList_Success(){
        //Mock data
        Project project = new Project();
        project.setId(1L);
        Bar bar = new Bar();
        List<Bar> barList = new ArrayList<>(List.of(bar));
        project.setBars(barList);
        //Mock behaviour
        when(projectDAO.getProjectById(1L)).thenReturn(project);
        //Execute method
        ResponseEntity<List<Bar>> responseEntity = getBarMethodRestApi.updatedTaskList(1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
    @Test
    public void testUpdateTaskList_NotFound(){
        //Mock data
        Project project = new Project();
        project.setId(1L);
        Bar bar = new Bar();
        List<Bar> barList = new ArrayList<>(List.of(bar));
        project.setBars(barList);
        //Mock behaviour
        when(projectDAO.getProjectById(2L)).thenReturn(null);
        //Execute method
        ResponseEntity<List<Bar>> responseEntity = getBarMethodRestApi.updatedTaskList(2L);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }


}
