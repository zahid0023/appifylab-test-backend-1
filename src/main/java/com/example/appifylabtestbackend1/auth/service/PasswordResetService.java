package com.example.appifylabtestbackend1.auth.service;

import com.example.appifylabtestbackend1.auth.dto.request.login.ResetPasswordRequest;
import com.example.appifylabtestbackend1.commons.dto.response.SuccessResponse;
import com.example.appifylabtestbackend1.auth.dto.response.VerifyOtpResponse;

public interface PasswordResetService {
    SuccessResponse forgotPassword(String username);

    SuccessResponse resetPassword(ResetPasswordRequest request);

    VerifyOtpResponse verifyOtpAndGetResetToken(String username, String otp);
}

