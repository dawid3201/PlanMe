package COMP390.PlanMe.RestControllers.Bar;

import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Entity.Bar;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class DeleteBarMethodRestApi {
    private final BarDAO barDAO;

    public DeleteBarMethodRestApi(BarDAO barDAO) {
        this.barDAO = barDAO;
    }
    @DeleteMapping("/project/deleteBar")
    public final ResponseEntity<Void> deleteBar(@RequestParam("barId") Long barId) {
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
}
