package com.queueup.dto.response;

public class ServeNextResponse {
    private String token;
    private String name;
    private String message;

    public ServeNextResponse() {}

    public ServeNextResponse(String token, String name, String message) {
        this.token = token;
        this.name = name;
        this.message = message;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}