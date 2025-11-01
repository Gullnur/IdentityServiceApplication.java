package com.example.identityservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String to, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Qeydiyyat təsdiq kodu");
        msg.setText("Sizin təsdiq kodunuz: " + code + "\nBu kod 10 dəqiqə ərzində etibarlıdır.");
        mailSender.send(msg);
    }
}

