package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.*;

import com.example.demo.dto.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    public String login(@ModelAttribute("user") User userForm, HttpServletResponse servletResponse) {
        user.setUsername(userForm.getUsername());
        user.setPassword(userForm.getPassword());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("X-OpenAM-Username", user.getUsername());
        httpHeaders.add("X-OpenAM-Password", user.getPassword());
        httpHeaders.add("Accept-API-Version", "resource=2.0, protocol=1.0");

        HttpEntity<MultiValueMap<String, String>> forgerockRequest =
                new HttpEntity<>(new LinkedMultiValueMap<>(), httpHeaders);

        try{
            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            "http://104.154.204.31/openam/json/realms/gog-demo/authenticate", forgerockRequest, String.class);
            //-------------- santier in lucru -------------------
            JSONObject jsonObject = new JSONObject(response.getBody());
            System.out.println(jsonObject);
            if(response.getStatusCode().is2xxSuccessful()){
                Cookie iplanetDirectoryPro = new Cookie("iplanetDirectoryPro", jsonObject.getString("tokenId"));
                servletResponse.addCookie(iplanetDirectoryPro);
            }
            //---------------------------------------------------
        }catch(HttpClientErrorException ex){
            return "fail";
        }

        return "success";
    }

    @PostMapping("/logout")
    public String logout(Model model, HttpServletRequest servletRequest, HttpServletResponse servletResponse){

        Optional<String> cookie = Arrays.stream(servletRequest.getCookies())
                .filter(c -> "iplanetDirectoryPro".equals(c.getName()))
                .map(Cookie::getValue)
                .findAny();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("iplanetDirectoryPro", cookie.get());
        httpHeaders.add("Cache-Control", "no-cache");
        httpHeaders.add("Accept-API-Version", "resource=2.0, protocol=1.0");
        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(new LinkedMultiValueMap<>(), httpHeaders);
        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://104.154.204.31/openam/json/realms/gog-demo/sessions/?_action=logout", request, String.class);
        model.addAttribute("user", new User());

        // delete cookie
        Cookie iplanetDirectoryPro = new Cookie("iplanetDirectoryPro", "");
        iplanetDirectoryPro.setMaxAge(0);
        servletResponse.addCookie(iplanetDirectoryPro);
        return "login";
    }

    @GetMapping("/info")
    public ResponseEntity<String> getSessionInfo(HttpServletRequest servletRequest){

        Optional<String> cookie = Arrays.stream(servletRequest.getCookies())
                .filter(c -> "iplanetDirectoryPro".equals(c.getName()))
                .map(Cookie::getValue)
                .findAny();


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("iplanetDirectoryPro", cookie.get());
        httpHeaders.add("Accept-API-Version", "resource=3.1, protocol=1.0");

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(new LinkedMultiValueMap<>(), httpHeaders);
        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://104.154.204.31/openam/json/realms/gog-demo/sessions/?_action=getSessionInfo", request, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());
        System.out.println(jsonObject);

        return response;
    }
}

