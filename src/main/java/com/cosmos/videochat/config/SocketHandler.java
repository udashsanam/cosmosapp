package com.cosmos.videochat.config;

import com.cosmos.login.repo.AppUserRepo;
import com.cosmos.videochat.dto.TextMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.modelmapper.internal.util.CopyOnWriteLinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {
    public static final Map<WebSocketSession, String> sessions = new CopyOnWriteLinkedHashMap<>();

    private final SimpMessagingTemplate simpMessagingTemplate;


    public SocketHandler(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            sessions.put(session, session.getHandshakeHeaders().get("userid").get(0));
            System.out.println("hello");
//            session.sendMessage(new TextMessage("Welcome cosmos"));
            TextMessageDto textMessageDto = new TextMessageDto();
            textMessageDto.setData("Websocket connected welcome to cosmos " + session.getHandshakeHeaders().get("userid").get(0));
            textMessageDto.setSender(session.getHandshakeHeaders().get(0).toString());
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(textMessageDto);
            session.sendMessage(new TextMessage(json));
//             session.sendMessage(new TextMessage("Websocket connected " + session.getHandshakeHeaders().get("userid").get(0)));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TextMessageDto textMessageDto = objectMapper.readValue(message.getPayload(), TextMessageDto.class);
            System.out.println("hello");
//            for (Map.Entry<WebSocketSession, String> entry:
//                    sessions.entrySet()) {
//                if(entry.getValue().equals(textMessageDto.getReceiver())){
//                    String sender = session.getHandshakeHeaders().get("userid").get(0);
//                    TextMessageDto textMessageDto1 = new TextMessageDto();
//
//                    textMessageDto1.setSender(sender);
//                    textMessageDto1.setData(textMessageDto.getData());
//
//                    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
//                    String json = ow.writeValueAsString(textMessageDto1);
//
//                    entry.getKey().sendMessage(new TextMessage(json));
//
//                }
//            }
            String sender = session.getHandshakeHeaders().get("userid").get(0);
            TextMessageDto textMessageDto1 = new TextMessageDto();

            textMessageDto1.setSender(sender);
            textMessageDto1.setData(textMessageDto.getData());

            simpMessagingTemplate.convertAndSendToUser(textMessageDto.getReceiver(), "/topic/video/chat", textMessageDto1);

        } catch (Exception ex){
            session.sendMessage(new TextMessage("Error sending message to receiver"));
        }




    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("disconnected");
        sessions.remove(session);
    }

    public void sendMessageToUser(String username, String message) throws IOException {
//        // Get the user's session from the map or data structure
//        WebSocketSession session = userSessions.get(username);
//
//        // Send the message to the user's session
//        session.sendMessage(new TextMessage(message));
    }


}
