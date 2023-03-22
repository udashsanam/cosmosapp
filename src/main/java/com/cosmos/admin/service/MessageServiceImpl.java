package com.cosmos.admin.service;

import com.cosmos.admin.entity.Message;
import com.cosmos.admin.repo.MessageRepo;
import com.cosmos.common.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl {

    @Autowired
    private MessageRepo messageRepo;

    public List<Message> fetchMessageByType(String type){
        List<Message> messages = messageRepo.selectMessageByType(type);

        if(messages.isEmpty()){
            throw new CustomException("No Message Found of type: " + type, HttpStatus.NOT_FOUND);
        }

        return messages;
    }


    public Message createMessage(Message message){
        return messageRepo.save(message);
    }

    public Message fetchMessageById(Long id) {
        return messageRepo.getOne(id);
    }

    public Message updateMessage(Long id, Message message) {
        Message fetchedMessage = fetchMessageById(id);

        if(fetchedMessage == null) {
            throw new CustomException("No Message Found of id: " + id, HttpStatus.NOT_FOUND);
        }

        fetchedMessage.setText(message.getText());
        fetchedMessage.setSendMessage(message.isSendMessage());

        return messageRepo.save(fetchedMessage);
    }

    public void deleteMessageByMessageId(Long id) {
        messageRepo.deleteById(id);
    }

}
