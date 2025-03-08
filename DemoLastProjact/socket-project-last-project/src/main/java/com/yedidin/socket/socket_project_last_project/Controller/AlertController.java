//package com.yedidin.socket.socket_project_last_project.Controller;
//
//import com.yedidin.socket.socket_project_last_project.WebSocket.AlertMessage;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class AlertController {
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @Autowired
//    public AlertController(SimpMessagingTemplate messagingTemplate) {
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    @MessageMapping("/event/new")
//    @SendTo("/topic/events")
//    public AlertMessage sendEvent(AlertMessage message) {
//        return message;
//    }
//
//    @MessageMapping("/volunteer/action")
//    public void handleVolunteerAction(AlertMessage message) {
//        messagingTemplate.convertAndSend("/topic/volunteer-actions", message);
//    }
//}


