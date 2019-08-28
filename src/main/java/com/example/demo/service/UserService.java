package com.example.demo.service;

import com.example.demo.dto.RegistrationRequest;
import com.example.demo.dto.User;
import com.example.demo.responses.*;
import com.example.demo.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public void sendLoginRequest(User user, HttpServletResponse servletResponse) throws HttpStatusCodeException{
        Authentication auth = authenticate(user);
        String code = getAuthorizationCode(auth);
        OAuthResponse oAuthResponse = getoAuthResponse(code);

        servletResponse.addCookie(new Cookie(Constants.IPLANET_DIRECTORY_PRO, auth.getTokenId()));
        servletResponse.addCookie(new Cookie(Constants.ACCESS_TOKEN, oAuthResponse.getAccessToken()));
    }

    public void sendLogoutRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        Optional<String> cookie = getTokenCookie(servletRequest, Constants.IPLANET_DIRECTORY_PRO);
        webClientBuilder.build()
                .post()
                .uri(Constants.LOGOUT_URL)
                .header(Constants.IPLANET_DIRECTORY_PRO, cookie.get())
                .header(HttpHeaders.CACHE_CONTROL, Constants.CACHE_CONTROL_VALUE)
                .header(Constants.ACCEPT_API_VERSION, Constants.ACCEPT_API_VERSION_VALUE)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                        Mono.error(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(SessionInfo.class)
                .block();

        // delete cookies
        deleteCookie(servletResponse, Constants.IPLANET_DIRECTORY_PRO);
        deleteCookie(servletResponse, Constants.ACCESS_TOKEN);
    }

    public SessionInfo getSessionInfo(HttpServletRequest servletRequest) {
        Optional<String> cookie = getTokenCookie(servletRequest, Constants.IPLANET_DIRECTORY_PRO);

        return cookie.isPresent() ? webClientBuilder.build()
                .post()
                .uri(Constants.SESSION_INFO_URL)
                .header(Constants.IPLANET_DIRECTORY_PRO, cookie.get())
                .header(Constants.ACCEPT_API_VERSION, Constants.ACCEPT_API_VERSION_VALUE)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                        Mono.error(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(SessionInfo.class)
                .block() : new SessionInfo();
    }

    public Introspect getIntrospect(HttpServletRequest servletRequest) {
        Optional<String> cookie = getTokenCookie(servletRequest, Constants.ACCESS_TOKEN);

        return cookie.isPresent() ? webClientBuilder.build()
                .post()
                .uri(Constants.INTROSPECT_URL)
                .header(Constants.ACCEPT_API_VERSION, Constants.ACCEPT_API_VERSION_VALUE)
                .header(HttpHeaders.AUTHORIZATION, Constants.AUTHORIZATION_VALUE)
                .body(BodyInserters.fromFormData("token", cookie.get()))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                        Mono.error(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(Introspect.class)
                .block() : new Introspect();
    }

    public void getRegistrationToken(HttpServletResponse servletResponse){
        RegistrationForm registrationForm = sendRegistrationFormRequest(Constants.EMPTY_BODY);

        servletResponse.addCookie(new Cookie(Constants.REGISTRATION_TOKEN, registrationForm.getToken()));
    }

    public void sendRegistrationRequest(User user, HttpServletRequest servletRequest, HttpServletResponse servletResponse){
        Optional<String> cookie = getTokenCookie(servletRequest, Constants.REGISTRATION_TOKEN);

        if(cookie.isPresent()){
            sendRegistrationFormRequest(new RegistrationRequest(user, cookie.get()));
            deleteCookie(servletResponse, Constants.REGISTRATION_TOKEN);
        }else{
            //display some error page
        }

    }

    private Authentication authenticate(User user) {
        return webClientBuilder.build()
                .post()
                .uri(Constants.LOGIN_URL)
                .header(Constants.X_OPEN_AM_USERNAME, user.getUserName())
                .header(Constants.X_OPEN_AM_PASSWORD, user.getPassword())
                .header(Constants.ACCEPT_API_VERSION, Constants.ACCEPT_API_VERSION_VALUE2)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                        Mono.error(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(Authentication.class)
                .block();
    }

    private String getAuthorizationCode(Authentication auth) {
        MultiValueMap<String,String> map = new LinkedMultiValueMap();
        map.add("client_id", "myClient");
        map.add("response_type", "code");
        map.add("scope", "myScope penguin");
        map.add("client_secret", "12345678");
        map.add("csrf", auth.getTokenId());
        map.add("redirect_uri", "http://localhost:3333/home");
        map.add("decision", "allow");

        ClientResponse clientResponse = webClientBuilder.build()
                .post()
                .uri(Constants.AUTHORIZE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .cookie(Constants.IPLANET_DIRECTORY_PRO, auth.getTokenId())
                .body(BodyInserters.fromFormData(map))
                .accept(MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                .exchange()
                .block();

        HttpHeaders headers = clientResponse.headers().asHttpHeaders();
        String location = headers.getLocation().toString();
        return location.substring(location.indexOf("code=") + 5, location.indexOf("&"));
    }

    private OAuthResponse getoAuthResponse(String code) {
        MultiValueMap<String,String> accessTokenBody = new LinkedMultiValueMap();
        accessTokenBody.add("code", code);
        accessTokenBody.add("grant_type", "authorization_code");
        accessTokenBody.add("client_id", "myClient");
        accessTokenBody.add("client_secret", "12345678");
        accessTokenBody.add("redirect_uri", Constants.REDIRECT_URL);

        return webClientBuilder.build()
                .post()
                .uri(Constants.ACCESS_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(accessTokenBody))
                .accept(MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResp ->
                        Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatus::is5xxServerError, clientResp ->
                        Mono.error(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(OAuthResponse.class)
                .block();
    }

    private RegistrationForm sendRegistrationFormRequest(Object body) {
        return webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("http")
                        .host("34.66.217.203")
                        .port(8080)
                        .path("/openidm/selfservice/registration")
                        .queryParam("_action", "submitRequirements")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(Constants.X_OPEN_IDM_NO_SESSION, "true")
                .header(Constants.X_OPEN_IDM_USERNAME, "anonymous")
                .header(Constants.X_OPEN_IDM_PASSWORD, "anonymous")
                .body(BodyInserters.fromObject(body))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                        Mono.error(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(RegistrationForm.class)
                .block();
    }

    private Optional<String> getTokenCookie(HttpServletRequest request, String cookieName) {
        return request.getCookies() != null ? Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .findAny() : Optional.empty();
    }

    private void deleteCookie(HttpServletResponse servletResponse, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        servletResponse.addCookie(cookie);
    }
}
