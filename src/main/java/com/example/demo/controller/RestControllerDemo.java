package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import org.json.*;

@Controller
public class RestControllerDemo {

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private WebApplicationContext context;

    private User user = new User();
    private String tokenId;
    private String successUrl;
    private String realm;

    @CrossOrigin
    @GetMapping("/get")
    public Map<String, String> getAuth() {
        Map<String, String> map = new HashMap<>();
        map.put("username", "alex");
        return map;
    }

    @GetMapping("/login")
    public String displayLogin(@ModelAttribute("user") User user){
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User userForm) {
        user.setUsername(userForm.getUsername());
        user.setPassword(userForm.getPassword());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-OpenAM-Username", user.getUsername());
        httpHeaders.add("X-OpenAM-Password", user.getPassword());
        httpHeaders.add("Accept-API-Version", "resource=2.0, protocol=1.0");

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(new LinkedMultiValueMap<>(), httpHeaders);

        try{
            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            "http://104.154.204.31/openam/json/realms/aaaaaaaaaaa/authenticate", request, String.class);

            //-------------- santier in lucru -------------------
            JSONObject jsonObject = new JSONObject(response.getBody());
            System.out.println(jsonObject);
            if(response.getStatusCode().is2xxSuccessful()){
                user.setTokenId(jsonObject.getString("tokenId"));
            }
            //---------------------------------------------------
        }catch(HttpClientErrorException ex){
            return "fail";
        }

        return "success";
    }

    @PostMapping("/logout")
    public String logout(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("iPlanetDirectoryPro", user.getTokenId());
        httpHeaders.add("Cache-Control", "no-cache");
        httpHeaders.add("Accept-API-Version", "resource=2.0, protocol=1.0");
        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(new LinkedMultiValueMap<>(), httpHeaders);
        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://104.154.204.31/openam/json/realms/aaaaaaaaaaa/sessions/?_action=logout", request, String.class);
        return "login";
    }
}


class User {
    private String username;
    private String password;
    private String tokenId;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTokenId() { return tokenId; }

    public void setTokenId(String tokenId) { this.tokenId = tokenId; }
}