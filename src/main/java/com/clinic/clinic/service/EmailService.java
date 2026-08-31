package com.clinic.clinic.service;

public interface EmailService {

    void sendPasswordResetEmail(String to, String resetLink, String name);
}
