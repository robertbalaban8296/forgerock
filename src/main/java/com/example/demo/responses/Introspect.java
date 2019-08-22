package com.example.demo.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Introspect {
    @JsonProperty("active")
    private boolean active;

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("client_id")
    private String client_id;

    @JsonProperty("user_id")
    private String user_id;

    @JsonProperty("token_type")
    private String token_type;

    @JsonProperty("exp")
    private String exp;

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("iss")
    private String iss;

    @JsonProperty("auth_level")
    private String auth_level;

    @JsonProperty("auditTrackingId")
    private String auditTrackingId;

    @JsonProperty("cts")
    private String cts;

    @JsonProperty("expires_in")
    private String expires_in;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getAuth_level() {
        return auth_level;
    }

    public void setAuth_level(String auth_level) {
        this.auth_level = auth_level;
    }

    public String getAuditTrackingId() {
        return auditTrackingId;
    }

    public void setAuditTrackingId(String auditTrackingId) {
        this.auditTrackingId = auditTrackingId;
    }

    public String getCts() {
        return cts;
    }

    public void setCts(String cts) {
        this.cts = cts;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    @Override
    public String toString() {
        return "Introspect{" +
                "active='" + active + '\'' +
                ", scope='" + scope + '\'' +
                ", client_id='" + client_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", token_type='" + token_type + '\'' +
                ", exp='" + exp + '\'' +
                ", sub='" + sub + '\'' +
                ", iss='" + iss + '\'' +
                ", auth_level='" + auth_level + '\'' +
                ", auditTrackingId='" + auditTrackingId + '\'' +
                ", cts='" + cts + '\'' +
                ", expires_in='" + expires_in + '\'' +
                '}';
    }
}
