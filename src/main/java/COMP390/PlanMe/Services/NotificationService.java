package COMP390.PlanMe.Services;

import COMP390.PlanMe.Entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyUsersOfUpdate() {
        messagingTemplate.convertAndSend("/topic/updates", "An update has occurred!");
    }
    public void taskUpdate() { //methopd for udpating any Task changes
        messagingTemplate.convertAndSend("/topic/updates", "THERE WAS AN UPDATE FOR A TASK ELEMENT");
    }
}
