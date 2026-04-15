package com.eq.charactertracker.controller;


import com.eq.charactertracker.model.User;
import com.eq.charactertracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @GetMapping("{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getUser(@PathVariable Long userId){
        return userService.getUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAllUsers(){
        return userService.getAllUsers();

    }


}
