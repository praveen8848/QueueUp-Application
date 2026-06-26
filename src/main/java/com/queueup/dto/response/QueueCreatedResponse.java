package com.queueup.dto.response;

public class QueueCreatedResponse {
    private String code;
    private String adminToken;  // NEW
    private String message;
    private String adminUrl;    // NEW

    public QueueCreatedResponse() {}

    public QueueCreatedResponse(String code, String adminToken, String message) {
        this.code = code;
        this.adminToken = adminToken;
        this.message = message;
        this.adminUrl = "/admin/" + code + "?token=" + adminToken;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getAdminToken() { return adminToken; }
    public void setAdminToken(String adminToken) { this.adminToken = adminToken; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAdminUrl() { return adminUrl; }
    public void setAdminUrl(String adminUrl) { this.adminUrl = adminUrl; }
}