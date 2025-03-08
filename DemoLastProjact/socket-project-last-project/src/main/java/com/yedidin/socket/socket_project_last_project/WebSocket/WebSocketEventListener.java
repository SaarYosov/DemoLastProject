//package com.yedidin.socket.socket_project_last_project.WebSocket;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.messaging.SessionConnectedEvent;
//import org.springframework.web.socket.messaging.SessionDisconnectEvent;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class WebSocketEventListener {
//    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
//
//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//        logger.info("משתמש התחבר!");
//    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        logger.info("משתמש התנתק!");
//    }
//}