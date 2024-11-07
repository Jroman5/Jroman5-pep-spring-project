package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.example.entity.Message;
import com.example.exception.UnacceptableMessageException;
import com.example.repository.MessageRepository;
@Service
public class MessageService {

    private MessageRepository mr;

    public MessageService(){}

    @Autowired
    public void setMessageRepository(MessageRepository msgrep){
        this.mr = msgrep;
    }


    public Message postMessage(Message messagePosted){
        if(messagePosted.getMessageText().isBlank() || messagePosted.getMessageText().length()>255){
            throw new UnacceptableMessageException();
        }
        return mr.save(messagePosted);
    }

    public List<Message> findAllMessages(){
        return this.mr.findAll();
    }

    public List<Message> findAllMessagesByUserId(Integer Id){
        List<Message> res = this.mr.findAllByPostedBy(Id);
        return res;
    }

    public Message findMessageById(Integer id){
        Optional<Message> res = this.mr.findById(id);
        return res.orElse(null);
    }

    public int deleteMessageById(Integer id){
        if(mr.existsById(id)){
            mr.deleteById(id);
            return 1;
        }
        return 0;
    }

    public int updateMessageById(Integer id, Message updatedMessage){
        if(updatedMessage.getMessageText().isBlank() || updatedMessage.getMessageText().isEmpty()){
            return 0;
        }
        if(updatedMessage.getMessageText().length() >255){
            return 0;
        }
        if(!mr.existsById(id)){
            return 0;
        }
        Message msgToUpdate = mr.getById(id);
        msgToUpdate.setMessageText(updatedMessage.getMessageText());
        Message res = mr.save(msgToUpdate);
        if(res.getMessageText().equals(updatedMessage.getMessageText())){
            return 1;
        }
        return 0;
    }
}
