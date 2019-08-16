package com.example.demo.service;

import com.example.demo.dto.User;
import com.example.demo.responses.Authentication;
import com.example.demo.responses.SessionInfo;
import com.example.demo.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
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

         Cookie iplanetDirectoryPro = new Cookie(Constants.IPLANET_DIRECTORY_PRO, auth.getTokenId());
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
