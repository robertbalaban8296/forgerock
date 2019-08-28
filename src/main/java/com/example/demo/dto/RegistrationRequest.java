package com.example.demo.dto;

public class RegistrationRequest {
    private Input input;

    private String token;

    public RegistrationRequest(User user, String token){
        this.token = token;
        createInput(user);
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void createInput(User user) {
        this.input = new Input(user);
    }

    private class Input{
        private User user;

        public Input(User user){
            setUser(user);
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
}


