package com.example.student_management.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("username")
    private String username;

    @SerializedName("token")
    private String token;

    public boolean isSuccess()  { return success; }
    public String getMessage()  { return message; }
    public String getUsername() { return username; }
    public String getToken()    { return token; }
}