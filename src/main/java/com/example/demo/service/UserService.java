package com.example.demo.service;

import com.example.demo.dto.User;
import com.example.demo.responses.Authentication;
import com.example.demo.responses.OAuthResponse;
import com.example.demo.responses.SessionInfo;
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
        Authentication auth = webClientBuilder.build()
                .post()
                .uri(Constants.LOGIN_URL)
                .header(Constants.X_OPEN_AM_USERNAME, user.getUsername())
                .header(Constants.X_OPEN_AM_PASSWORD, user.getPassword())
                .header(Constants.ACCEPT_API_VERSION, Constants.ACCEPT_API_VERSION_VALUE2)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                        Mono.error(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(Authentication.class)
                .block();

        MultiValueMap<String,String> map = new LinkedMultiValueMap();
        map.add("client_id", "myClient");
        map.add("response_type", "code");
        map.add("scope", "myScope");
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
        String code = location.substring(location.indexOf("code=") + 5, location.indexOf("&"));


        MultiValueMap<String,String> accessTokenBody = new LinkedMultiValueMap();
        accessTokenBody.add("code", code);
        accessTokenBody.add("grant_type", "authorization_code");
        accessTokenBody.add("client_id", "myClient");
        accessTokenBody.add("client_secret", "12345678");
        accessTokenBody.add("redirect_uri", Constants.REDIRECT_URL);

        OAuthResponse oAuthResponse = webClientBuilder.build()
                .post()
                .uri(Constants.ACCESS_TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                //.cookie(Constants.IPLANET_DIRECTORY_PRO, auth.getTokenId())
                .body(BodyInserters.fromFormData(accessTokenBody))
                .accept(MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResp ->
                        Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatus::is5xxServerError, clientResp ->
                        Mono.error(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(OAuthResponse.class)
                .block();

        Cookie iplanetDirectoryPro = new Cookie(Constants.IPLANET_DIRECTORY_PRO, oAuthResponse.getAccessToken());
        servletResponse.addCookie(iplanetDirectoryPro);
    }

    public void sendLogoutRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        Optional<String> cookie = getTokenCookie(servletRequest);
        webClientBuilder.build()
                .post()
                .uri(Constants.LOGOUT_URL)
                .header(Constants.IPLANET_DIRECTORY_PRO, cookie.get())
                .header(Constants.CACHE_CONTROL, Constants.CACHE_CONTROL_VALUE)
                .header(Constants.ACCEPT_API_VERSION, Constants.ACCEPT_API_VERSION_VALUE)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new HttpClientErrorException(HttpStatus.BAD_REQUEST)))
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                        Mono.error(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)))
                .bodyToMono(SessionInfo.class)
                .block();

        // delete cookie
        Cookie iplanetDirectoryPro = new Cookie(Constants.IPLANET_DIRECTORY_PRO, "");
        iplanetDirectoryPro.setMaxAge(0);
        servletResponse.addCookie(iplanetDirectoryPro);
    }

    public SessionInfo getSessionInfo(HttpServletRequest servletRequest) {
        Optional<String> cookie = getTokenCookie(servletRequest);

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

    private Optional<String> getTokenCookie(HttpServletRequest request) {
        return request.getCookies() != null ? Arrays.stream(request.getCookies())
                .filter(c -> Constants.IPLANET_DIRECTORY_PRO.equals(c.getName()))
                .map(Cookie::getValue)
                .findAny() : Optional.empty();
    }
}
