package COMP390.PlanMe.Bar;


import COMP390.PlanMe.Bar.Service.DeleteBarService;
import COMP390.PlanMe.Bar.Service.GetBarService;
import COMP390.PlanMe.Bar.Service.PatchBarService;
import COMP390.PlanMe.Bar.Service.PostBarService;
import COMP390.PlanMe.Exceptions.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bar")
@AllArgsConstructor
public class BarRestController {
    private DeleteBarService deleteBarService;
    private GetBarService getBarService;
    private PatchBarService patchBarService;
    private PostBarService postBarService;
    //-------------------------------------DELETE-METHOD-----------------------------------
    @DeleteMapping("/deleteBar")
    public ResponseEntity<String> deleteBar(@RequestParam("barId") Long barId) throws NotFoundException {
        return ResponseEntity.ok(deleteBarService.deleteBar(barId));
    }
    //-------------------------------------GET-METHODS-----------------------------------
    @GetMapping("/getUpdatedBars/{projectId}")//Used for testing
    public ResponseEntity<List<Bar>> updateTaskList(@PathVariable("projectId") Long projectId){
        return ResponseEntity.ok(getBarService.updatedTaskList(projectId));
    }
    //-------------------------------------PATCH-METHODS-----------------------------------
    @PatchMapping("/updateBarPosition")
    public ResponseEntity<Map<String, Long>> updateBarPosition(@RequestParam("barId") Long barId,
                                                               @RequestParam("newPosition")Long newPosition){
        return ResponseEntity.ok(patchBarService.updateBarPosition(barId, newPosition));
    }
    @PatchMapping("/updateBarName")
    public ResponseEntity<Bar> updateBarName(@RequestParam("barId") Long barId,
                                             @RequestParam("newPosition")String barName){
        return ResponseEntity.ok(patchBarService.updateBarName(barId, barName));
    }
    //-------------------------------------POST-METHODS-----------------------------------
    @PostMapping("/addBar")
    public ResponseEntity<Bar> addBar(@RequestParam("projectId") Long projectId, @RequestParam("barName") String barName){
        return ResponseEntity.ok(postBarService.addBar(projectId, barName));
    }
}
