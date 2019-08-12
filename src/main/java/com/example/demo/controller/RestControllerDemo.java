package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import org.json.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RestControllerDemo {

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private WebApplicationContext context;

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
    public String displayLogin(@ModelAttribute("user") User user ){
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user, BindingResult bindingResult) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-OpenAM-Username", user.getUsername());
        httpHeaders.add("X-OpenAM-Password", user.getPassword());
        httpHeaders.add("Accept-API-Version", "resource=2.0, protocol=1.0");

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(new LinkedMultiValueMap<>(), httpHeaders);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://104.154.204.31/openam/json/realms/aaaaaaaaaaa/authenticate", request, String.class);

        //-------------- santier in lucru -------------------
        JSONObject jsonObject = new JSONObject(response.getBody());
        System.out.println(jsonObject);
        //---------------------------------------------------

        if(response.getStatusCode().is2xxSuccessful()){
            return "success";
        }else{
            return "fail";
        }

    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> map = new HashMap<>();
        map.put("resp", "hello");
        // jwt , add
        return map;
    }
}

class User {
    private String username;
    private String password;

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
}