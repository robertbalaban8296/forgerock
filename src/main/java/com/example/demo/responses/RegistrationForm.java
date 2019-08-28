package com.example.demo.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistrationForm {
    @JsonProperty("type")
    private String type;

    @JsonProperty("tag")
    private String tag;

//    @JsonProperty("requirements")
//    private String requirements;

    @JsonProperty("token")
    private String token;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "RegistrationForm{" +
                "type='" + type + '\'' +
                ", tag='" + tag + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
