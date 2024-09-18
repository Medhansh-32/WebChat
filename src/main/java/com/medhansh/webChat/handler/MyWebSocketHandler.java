package com.medhansh.webChat.handler;

import com.medhansh.webChat.entity.Message;
import com.medhansh.webChat.repositories.ContactRespository;
import com.medhansh.webChat.service.ContactService;
import com.medhansh.webChat.service.MessageService;
import com.medhansh.webChat.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//@Slf4j
//public class MyWebSocketHandler extends TextWebSocketHandler {
//
//    private static final Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);
//
//    public int users=0;
//
//    // Store active WebSocket sessions mapped to user IDs
//    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
//
////    @Override
////    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//////        HttpSession httpSession = (HttpSession) session.getAttributes().get("httpSession");
//////        String userId="";
//////    if(httpSession!=null) {
//////       userId= httpSession.getAttribute("userId").toString();
//////        logger.info(userId);
//////       sessions.put(userId, session);
//////    } else{
//////        logger.info("HttpSession is null");
//////    }
////        users++;
////        logger.info("User "+users+" joined id: " +session.getId());
////
////    sessions.put(session.getId(), session);
////    }
//@Override
//public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//    // Retrieve the HttpSession from the WebSocket session attributes
//    HttpSession httpSession = (HttpSession) session.getAttributes().get("httpSession");
//
//    if (httpSession != null) {
//        // Retrieve the userId from the HttpSession
//        String userId = (String) httpSession.getAttribute("userId");
//        if (userId != null) {
//            // Log and store the WebSocket session against the userId
//            log.info("User ID: " + userId);
//            sessions.put(userId, session);
//        } else {
//            log.error("userId is null in HttpSession");
//        }
//    } else {
//        log.error("HttpSession is null in WebSocket session");
//    }
//}
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        logger.info("Message received from " + session.getId() + ": " + message.getPayload());
//
//        String[] parts = message.getPayload().split(":", 2);
//        if (parts.length == 2) {
//            String targetUserId = parts[0];
//            String messageToSend = parts[1];
//            WebSocketSession targetSession = sessions.get(targetUserId);
//
//            if (targetSession != null && targetSession.isOpen()) {
//                targetSession.sendMessage(new TextMessage("Message from " + targetUserId + ": " + messageToSend));
//            } else {
//                session.sendMessage(new TextMessage("User " + targetUserId + " is not connected."));
//            }
//        } else {
//            session.sendMessage(new TextMessage("Invalid message format. Use 'targetUserId:message'."));
//        }
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        sessions.values().remove(session);
//        logger.info("WebSocket session closed: " + session.getId());
//    }
//}

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);
    private final ContactService contactService;
    private final MessageService messageService;
    private final UserService userService;
    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    public MyWebSocketHandler(ContactService contactService, MessageService messageService, UserService userService) {
        this.contactService = contactService;
        this.messageService = messageService;
        this.userService = userService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        HttpSession httpSession = (HttpSession) session.getAttributes().get("httpSession");
        String sessionId = session.getAttributes().get("sessionId").toString();
        String receiver=session.getAttributes().get("receiver").toString();

        logger.info("new User Joined : "+sessionId);
//        if (httpSession != null) {
//            String userId = (String) httpSession.getAttribute("userId");
//            if (userId != null) {
//                logger.info("WebSocket session established for user: " + userId);
//                sessions.put(userId, session);
//            } else {
//                logger.error("User ID is null in HttpSession");
//            }
//        } else {
//            logger.error("HttpSession is null in WebSocket session");
//        }
        sessions.put(sessionId, session);
        for(String id:sessions.keySet()){
            logger.info(id+" : "+sessions.get(id).getId());
        }

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Message received from " + session.getId() + ": " + message.getPayload());

        String receiver=session.getAttributes().get("receiver").toString();


        String[] parts = message.getPayload().split(":", 2);
        if (parts.length == 2) {

            String messageToSend = parts[1];
            logger.info(receiver);
           logger.info(messageToSend);
            WebSocketSession targetSession = sessions.get(receiver);
            String sender =session.getAttributes().get("sessionId").toString();

            if (targetSession != null && targetSession.isOpen()) {
                targetSession.sendMessage(new TextMessage("Message from " + session.getAttributes().get("sessionId").toString() + ": " + messageToSend));

                messageService.saveMessage(sender,receiver,messageToSend);

                Message message1= Message.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .date(new Date())
                        .message(messageToSend)
                        .build();
                userService.saveUserMessage(sender,receiver,message1);
            } else {
                messageService.saveMessage(sender,receiver,messageToSend);
                Message message1= Message.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .date(new Date())
                        .message(messageToSend)
                        .build();
                contactService.saveContact(sender);
            }
        } else {
            session.sendMessage(new TextMessage("Invalid message format. Use 'targetUserId:message'."));
        }
    }

}
