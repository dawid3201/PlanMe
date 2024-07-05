package COMP390.PlanMe.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskChangeNotification {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    public void taskUpdate() { //Method used for notifying about changes, not yet used
        messagingTemplate.convertAndSend("/topic/updates",
                "THERE WAS AN UPDATE FOR A TASK ELEMENT");
    }
}
