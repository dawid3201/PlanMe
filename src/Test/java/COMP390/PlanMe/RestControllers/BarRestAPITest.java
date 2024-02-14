package COMP390.PlanMe.RestControllers;

import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Dao.ProjectDAO;
import COMP390.PlanMe.Dao.TaskDAO;
import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.RestControllers.Bar.DeleteBarMethodRestApi;
import COMP390.PlanMe.RestControllers.Task.DeleteTaskMethodRestApi;
import COMP390.PlanMe.Services.NotificationService;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BarRestAPITest {
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
    void DeleteBarTest(){
        //Mock dependencies
        BarDAO barDAO = mock(BarDAO.class);
        DeleteBarMethodRestApi barRestController = new DeleteBarMethodRestApi(barDAO);

        //Test data
        Bar mockBar = new Bar();
        Long barId1 = 1L;
        int barPosition1 = 1;
        mockBar.setId(barId1);
        mockBar.setPosition(barPosition1);
        List<Bar> barList = new ArrayList<>();
        barList.add(mockBar);

        //Mock behaviour
        when(barDAO.getBarById(barId1)).thenReturn(mockBar);
        //Execute method
        ResponseEntity<Void> responseEntity = barRestController.deleteBar(barId1);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }


}
