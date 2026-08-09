package com.dhvestudent.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordReset(String to, String resetUrl) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(to);
        msg.setSubject("[DHV E-STUDENT] Đặt lại mật khẩu");
        msg.setText("Xin chào,\n\nBạn đã yêu cầu đặt lại mật khẩu. " +
                    "Vui lòng nhấp vào liên kết sau để tiếp tục:\n" + resetUrl +
                    "\n\nLiên kết sẽ hết hạn sau 24 giờ.\n\nTrân trọng,\nDHV E-STUDENT");
        mailSender.send(msg);
    }
}
