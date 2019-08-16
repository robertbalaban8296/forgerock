package com.example.demo.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class SessionInfo {
    @JsonProperty("username")
    private String name;

    @JsonProperty("universalId")
    private String universalId;

    @JsonProperty("realm")
    private String realm;

    @JsonProperty("latestAccessTime")
    private String latestAccessTime;

    @JsonProperty("maxIdleExpirationTime")
    private String maxIdleExpirationTime;

    @JsonProperty("maxSessionExpirationTime")
    private String maxSessionExpirationTime;

    private String AMCtxId;

    @JsonProperty("properties")
    private void unpackAMCtxId(Map<String,Object> properties){
        AMCtxId = (String)properties.get("AMCtxId");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniversalId() {
        return universalId;
    }

    public void setUniversalId(String universalId) {
        this.universalId = universalId;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getLatestAccessTime() {
        return latestAccessTime;
    }

    public void setLatestAccessTime(String latestAccessTime) {
        this.latestAccessTime = latestAccessTime;
    }

    public String getMaxIdleExpirationTime() {
        return maxIdleExpirationTime;
    }

    public void setMaxIdleExpirationTime(String maxIdleExpirationTime) {
        this.maxIdleExpirationTime = maxIdleExpirationTime;
    }

    public String getMaxSessionExpirationTime() {
        return maxSessionExpirationTime;
    }

    public void setMaxSessionExpirationTime(String maxSessionExpirationTime) {
        this.maxSessionExpirationTime = maxSessionExpirationTime;
    }

    public String getAMCtxId() {
        return AMCtxId;
    }

    public void setAMCtxId(String AMCtxId) {
        this.AMCtxId = AMCtxId;
    }

    @Override
    public String toString() {
        return "SessionInfo{" +
                "name='" + name + '\'' +
                ", universalId='" + universalId + '\'' +
                ", realm='" + realm + '\'' +
                ", latestAccessTime='" + latestAccessTime + '\'' +
                ", maxIdleExpirationTime='" + maxIdleExpirationTime + '\'' +
                ", maxSessionExpirationTime='" + maxSessionExpirationTime + '\'' +
                ", AMCtxId='" + AMCtxId + '\'' +
                '}';
    }
}
