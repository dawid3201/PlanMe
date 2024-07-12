package COMP390.PlanMe.User.ProjectMember;

import COMP390.PlanMe.Exceptions.TaskNotFoundException;
import COMP390.PlanMe.Exceptions.UserAlreadyAssignedException;
import COMP390.PlanMe.Exceptions.UserNotFoundException;
import COMP390.PlanMe.User.ProjectMember.Service.GetMemberService;
import COMP390.PlanMe.User.ProjectMember.Service.PatchMemberService;
import COMP390.PlanMe.User.ProjectMember.Service.PostMemberService;
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
    public ResponseEntity<String> assignUser(@RequestParam("userEmail") String userEmail, @RequestParam("taskId") Long taskId) throws UserNotFoundException, UserAlreadyAssignedException, TaskNotFoundException {
        return ResponseEntity.ok(patchMemberService.assignUser(userEmail, taskId));
    }
}
