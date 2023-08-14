//package COMP390.PlanMe.webSocket;
//
//import jakarta.websocket.OnClose;
//import jakarta.websocket.OnMessage;
//import jakarta.websocket.OnOpen;
//import jakarta.websocket.Session;
//import org.springframework.stereotype.Component;
//
//import javax.websocket.*;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.Set;
//import java.util.concurrent.CopyOnWriteArraySet;
//
//@ServerEndpoint(value = "/broadcast")
//@Component // Let Spring manage this as a bean
//public class Broadcaster {
//
//    private static Set<Session> sessions = new CopyOnWriteArraySet<>();
//
//    @OnOpen
//    public void onOpen(Session session) {
//        sessions.add(session);
//    }
//
//    @OnClose
//    public void onClose(Session session) {
//        sessions.remove(session);
//    }
//
//    @OnMessage
//    public void onMessage(String message) {
//        broadcast(message);
//    }
//
//    // A method to send a message to all connected sessions
//    public void broadcast(String message) {
//        for (Session session : sessions) {
//            try {
//                session.getBasicRemote().sendText(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
