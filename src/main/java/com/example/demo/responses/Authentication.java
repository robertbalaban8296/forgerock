package com.example.demo.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Authentication {
    @JsonProperty("tokenId")
    private String tokenId;
    @JsonProperty("successUrl")
    private String successUrl;
    @JsonProperty("realm")
    private String realm;

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    @Override
    public String toString() {
        return "Authentication{" +
                "tokenId='" + tokenId + '\'' +
                ", successUrl='" + successUrl + '\'' +
                ", realm='" + realm + '\'' +
                '}';
    }
}
