package com.example.demo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import org.json.*;
@RestController
public class RestControllerDemo {


    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private WebApplicationContext context;

    private String tokenId;
    private String successUrl;
    private String realm;
    private User user;
    @CrossOrigin
    @GetMapping("/get")
    public Map<String, String> getAuth() {
        Map<String, String> map = new HashMap<>();
        map.put("username", "alex");
        return map;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User json) {


        // admin
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-OpenAM-Username", "robert");
        httpHeaders.add("X-OpenAM-Password", "robertrobert");
        httpHeaders.add("Accept-API-Version", "resource=2.0, protocol=1.0");

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(new LinkedMultiValueMap<>(), httpHeaders);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://104.154.204.31/openam/json/realms/root/authenticate", request, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());
        tokenId = jsonObject.getString("tokenId");
        successUrl = jsonObject.getString("successUrl");
        realm = jsonObject.getString("realm");
        user = json;
        // Cookie name iPlanetDirectoryPro

        System.out.println(jsonObject);



        return new ResponseEntity<String>(HttpStatus.OK);
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
    private char[] password;

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}