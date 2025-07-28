package com.example.recycling_app.dto;

import com.google.gson.annotations.SerializedName;

public class FindEmailResponse {
    @SerializedName("email")
    private String email;

    public FindEmailResponse(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
