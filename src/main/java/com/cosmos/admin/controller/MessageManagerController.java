package com.cosmos.admin.controller;

import com.cosmos.admin.entity.Message;
import com.cosmos.admin.service.MessageServiceImpl;
import com.cosmos.common.model.ApiMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class MessageManagerController {

    @Autowired
    private MessageServiceImpl messageService;

    @GetMapping(value = "/messages", produces = "application/json")
    public List<Message> getMessage(@RequestParam("type") String type) {
        return messageService.fetchMessageByType(type);
    }

    @PostMapping(value = "/messages", produces = "application/json", consumes = "application/json")
    public Message processSaveMessage(@RequestBody Message message) {
        return messageService.createMessage(message);
    }

    @DeleteMapping(value = "/messages/{id}", produces = "application/json")
    public ApiMessage deleteMessage(@PathVariable Long id) {
        messageService.deleteMessageByMessageId(id);
        return new ApiMessage("Successfully deleted!", HttpStatus.OK);
    }

    @PutMapping(value = "/messages/{id}")
    public Message updateMessage(@PathVariable Long id, @RequestBody Message message) {
        return messageService.updateMessage(id, message);
    }
}
