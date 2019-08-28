package com.example.demo.utils;

public class Constants {
    //cookies
    public static final String IPLANET_DIRECTORY_PRO = "iplanetDirectoryPro";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REGISTRATION_TOKEN = "registrationToken";

    //headers
    public static final String X_OPEN_AM_USERNAME = "X-OpenAM-Username";
    public static final String X_OPEN_AM_PASSWORD = "X-OpenAM-Password";
    public static final String X_OPEN_IDM_NO_SESSION = "X-OpenIDM-NoSession";
    public static final String X_OPEN_IDM_USERNAME = "X-OpenIDM-Username";
    public static final String X_OPEN_IDM_PASSWORD = "X-OpenIDM-Password";
    public static final String ACCEPT_API_VERSION = "Accept-API-Version";

    //header values
    public static final String ACCEPT_API_VERSION_VALUE = "resource=3.1, protocol=1.0";
    public static final String ACCEPT_API_VERSION_VALUE2 = "resource=2.0, protocol=1.0";
    public static final String CACHE_CONTROL_VALUE = "no-cache";
    public static final String AUTHORIZATION_VALUE= "Basic bXlDbGllbnQ6MTIzNDU2Nzg=";

    //body
    public static final String EMPTY_BODY= "{\"input\":{\"input\":{}}}";

    //URLs
    public static final String LOGIN_URL = "http://34.68.88.95/openam/json/realms/Demo/authenticate";
    public static final String LOGOUT_URL = "http://34.68.88.95/openam/json/realms/Demo/sessions/?_action=logout";
    public static final String SESSION_INFO_URL = "http://34.68.88.95/openam/json/realms/Demo/sessions/?_action=getSessionInfo";
    public static final String AUTHORIZE_URL = "http://34.68.88.95/openam/oauth2/Demo/authorize";
    public static final String ACCESS_TOKEN_URL = "http://34.68.88.95/openam/oauth2/Demo/access_token";
    public static final String INTROSPECT_URL = "http://34.68.88.95/openam/oauth2/Demo/introspect";
    public static final String REDIRECT_URL = "http://localhost:3333/home";
}
