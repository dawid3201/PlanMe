package COMP390.PlanMe.Services.Bar;

import COMP390.PlanMe.Dao.BarDAO;
import COMP390.PlanMe.Entity.Bar;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class DeleteBarService {
    private final BarDAO barDAO;
    public final Void deleteBar(Long barId) {
        Bar bar = barDAO.getBarById(barId);
        if (bar != null) {
            List<Bar> barsToUpdate = barDAO.getBarsByProjectAndPositionGreaterThan(bar.getProject(), bar.getPosition());
            for(Bar barToUpdate : barsToUpdate){
                barToUpdate.setPosition(barToUpdate.getPosition()-1);
                barDAO.save(barToUpdate);
            }
            barDAO.delete(bar);
        }
        return null;
    }
}
