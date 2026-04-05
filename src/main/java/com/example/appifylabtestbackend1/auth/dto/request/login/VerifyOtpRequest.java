package com.example.appifylabtestbackend1.auth.dto.request.login;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @JsonAlias("user_name")
    private String userName;
    private String otp;
}