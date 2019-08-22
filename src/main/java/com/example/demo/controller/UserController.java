package com.example.demo.controller;
import com.example.demo.responses.Introspect;
import com.example.demo.responses.SessionInfo;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.User;
import org.springframework.web.client.HttpStatusCodeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String displayHome(HttpServletRequest servletRequest, Model model){
        Introspect introspect = userService.getIntrospect(servletRequest);
        model.addAttribute("introspect", introspect);
        return "home";
    }

    @GetMapping("/login")
    public String displayLoginForm(@ModelAttribute("user") User user){
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user, HttpServletResponse servletResponse) {
        try{
            userService.sendLoginRequest(user, servletResponse);
        }catch(HttpStatusCodeException ex){
            return "fail";
        }
        return "successfullLogin";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest servletRequest, HttpServletResponse servletResponse){
        userService.sendLogoutRequest(servletRequest, servletResponse);
        return "successfullLogout";
    }

    @GetMapping("/register")
    public String displayRegistrationForm(@ModelAttribute("user") User user){
        return "register";
    }

    @GetMapping("/introspect")
    public String testIntrospect(HttpServletRequest request, HttpServletResponse response){
        Introspect introspect = userService.getIntrospect(request);
        if(introspect.isActive()){
            return "resource2";
        }else{
            return "resource1";
        }

    }

    @PostMapping("/register")
    public String registerUser(){
        return "login";
    }
}

