package com.example.demo.user.controller.port;

public interface CertificateService {
    void send(String email, long userId, String certificationCode);
}
