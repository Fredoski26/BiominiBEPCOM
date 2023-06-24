package com.example.bepcom.model;

import com.google.gson.annotations.SerializedName;

public class PassportRequest {
    @SerializedName("passport")
    private String passport; //base64 string

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }
}
