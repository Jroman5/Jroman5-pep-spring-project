package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.repository.AccountRepository;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.example.entity.Account;
import com.example.exception.DuplicateUserException;
import com.example.exception.UnauthorizedException;

@Service
public class AccountService {
    private AccountRepository ar;

    @Autowired
    public AccountService(AccountRepository accRepo){
        this.ar = accRepo;
    }

    public Account login(Account attempt){
        Optional<Account> acc = ar.findAccountByUsername(attempt.getUsername());
        Account user = null;
        if(acc.isPresent()){
            user = acc.get();
        } else{
            throw new UnauthorizedException();
        }
        if(attempt.getPassword().equals(user.getPassword())){
            return user;
        }
        throw new UnauthorizedException();
    }

    private Account findAccountByUsername(Account acc){
        Optional<Account> res = ar.findAccountByUsername(acc.getUsername());
        if(res.isPresent()){
            return res.get();
        }
        return null;
    }

    public Account createAccount(Account acc){
        //consider throwing an invalid field exception
        if(acc.getPassword().length() < 4){
            return null;
        }
        if(acc.getUsername().isBlank()){
            return null;
        }
        //throw an accountExistsException
        if(findAccountByUsername(acc) != null){
            throw new DuplicateUserException();
        }
        return ar.save(acc);

    }

    public boolean accountExists(int Id){
        return ar.existsById(Id);
    }

    
}
