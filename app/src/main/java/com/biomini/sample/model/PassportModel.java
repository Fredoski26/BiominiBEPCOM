package com.example.bepcom.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PassportModel {

        @SerializedName("status")
        @Expose
        private String status;

        @SerializedName("status_code")
        @Expose
        private float status_code;
        @SerializedName("message")
        @Expose
        private String message;


        // Getter Methods

        public String getStatus() {
        return status;
    }

        public float getStatus_code() {
        return status_code;
    }

        public String getMessage() {
        return message;
    }

        // Setter Methods

        public void setStatus(String status) {
        this.status = status;
    }

        public void setStatus_code(float status_code) {
        this.status_code = status_code;
    }

        public void setMessage(String message) {
        this.message = message;
    }
    }


