package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


import com.example.service.AccountService;
import com.example.service.MessageService;
import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.DuplicateUserException;
import com.example.exception.UnacceptableMessageException;
import com.example.exception.UnauthorizedException;


/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {
    private AccountService as;
    private MessageService ms;

    public SocialMediaController(){}
    

    @Autowired
    public void setAccountService(AccountService accService){
        this.as = accService;
    }
    @Autowired
    public void setMessageService(MessageService msgService){
        this.ms = msgService;
    }

    @PostMapping("/register")
    public ResponseEntity<Account> RegisterAccount(@RequestBody Account newAccount){
        Account res = null;
        res = as.createAccount(newAccount);
        return ResponseEntity.status(200)
            .body(res);

    }


    @PostMapping("/login")
    public ResponseEntity<Account> loginIntoAccount(@RequestBody Account acc){
        Account res = null;
        res = as.login(acc);
        return ResponseEntity.status(200).body(res);
    }


    @PostMapping("/messages")
    public ResponseEntity<Message> postMessage(@RequestBody Message msg){
            int postedBy = msg.getPostedBy();
            boolean exists = this.as.accountExists(postedBy);
            if(!exists){
                return ResponseEntity.status(400).body(null);
            }
        
        
        
        Message res = ms.postMessage(msg);
        return ResponseEntity.status(200).body(res);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages(){
        List<Message> res = ms.findAllMessages();
        return ResponseEntity.status(200).body(res);

    }

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getAllMessageByUser(@PathVariable("accountId") Integer accountId){
        List<Message> res = this.ms.findAllMessagesByUserId(accountId);
        return ResponseEntity.status(200).body(res);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable("messageId") Integer messageId){
        Message res = this.ms.findMessageById(messageId);
        return ResponseEntity.status(200).body(res);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessageById(@PathVariable ("messageId") Integer messageId){
        int res = ms.deleteMessageById(messageId);
        if(res > 0){
            return ResponseEntity.status(200).body(res);
        }
        return ResponseEntity.status(200).body(null);

    }
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessageById(@PathVariable("messageId") Integer messageId, @RequestBody Message messageText){
        int res = ms.updateMessageById(messageId, messageText);
        if(res == 0){
            return ResponseEntity.status(400).body(null);
        }
        return ResponseEntity.status(200).body(res);


    } 
    
    @ExceptionHandler(DuplicateUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateUserException(DuplicateUserException ex){
        return "Duplicate User Exception: " + ex.getMessage();
    }

    

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorizedException(UnauthorizedException ex){
        return "Unauthorized user error: " + ex.getMessage();
    }

    @ExceptionHandler(UnacceptableMessageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleUnacceptableMessageException(UnacceptableMessageException ex){
        return  "Unacceptable message: " + ex.getMessage();
    }
 
}
