package COMP390.PlanMe.RestControllers.Member;

import COMP390.PlanMe.Services.Member.GetMemberService;
import COMP390.PlanMe.Services.Member.PatchMemberService;
import COMP390.PlanMe.Services.Member.PostMemberService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@AllArgsConstructor
public class MemberRestController {
    private GetMemberService getMemberService;
    private PostMemberService postMemberService;
    private PatchMemberService patchMemberService;
    //-----------------------------------------GET-METHODS--------------------------------
    @GetMapping("/getMembers/{projectId}") //Used in PROJECT.JS
    public ResponseEntity<String> getTaskName(@PathVariable Long projectId) {
        return ResponseEntity.ok(getMemberService.getTaskName(projectId));
    }
    @GetMapping("/getInitials/{email}") //Unused, for the future
    public ResponseEntity<String> getInitials(@PathVariable String email){
        return ResponseEntity.ok(getMemberService.getInitials(email));
    }
    //-----------------------------------------POST-METHODS--------------------------------
    @PostMapping("/addMember") //Used in PROJECT.JS
    public ResponseEntity<Void> addMember(@RequestParam("projectId") Long projectId, @RequestParam("memberEmail") String memberEmail) {
        return ResponseEntity.ok(postMemberService.addMember(projectId, memberEmail));
    }
    //-----------------------------------------PATCH-METHODS--------------------------------
    @PatchMapping("/assignUserToTask") //USED in TASK.JS
    public ResponseEntity<Boolean> assignUser(@RequestParam("userEmail") String userEmail, @RequestParam("taskId") Long taskId) {
        return ResponseEntity.ok(patchMemberService.assignUser(userEmail, taskId));
    }
}
