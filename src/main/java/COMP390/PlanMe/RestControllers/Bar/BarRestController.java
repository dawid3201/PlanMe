package COMP390.PlanMe.RestControllers.Bar;

import COMP390.PlanMe.Entity.Bar;
import COMP390.PlanMe.Services.Bar.DeleteBarService;
import COMP390.PlanMe.Services.Bar.GetBarService;
import COMP390.PlanMe.Services.Bar.PatchBarService;
import COMP390.PlanMe.Services.Bar.PostBarService;
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
    public ResponseEntity<Void> deleteBar(@RequestParam("barId") Long barId) {
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
