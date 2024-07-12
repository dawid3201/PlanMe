package COMP390.PlanMe.Bar.Service;

import COMP390.PlanMe.Bar.BarDAO;
import COMP390.PlanMe.Bar.Bar;
import COMP390.PlanMe.Exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@AllArgsConstructor
public class DeleteBarService {
    private final BarDAO barDAO;
    public final String deleteBar(Long barId) throws NotFoundException {
        Bar bar = barDAO.getBarById(barId);
        if(bar == null){
            throw new NotFoundException("Bar was not found.");
        }
        List<Bar> barsToUpdate = barDAO.getBarsByProjectAndPositionGreaterThan(bar.getProject(), bar.getPosition());
        for(Bar barToUpdate : barsToUpdate){
            barToUpdate.setPosition(barToUpdate.getPosition()-1);
            barDAO.save(barToUpdate);
        }
        barDAO.delete(bar);
        return "Bar with ID: " + barId + " was deleted.";
    }
}
