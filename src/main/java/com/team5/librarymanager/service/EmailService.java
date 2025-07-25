package com.team5.librarymanager.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Gửi mail khi hoàn thành mượn sách
    public void sendLoanCompletionMail(String toEmail, String bookTitle, LocalDateTime returnDate) {
        String subject = "Loan Completion Notification";
        String body = "Dear User,\n\n" +
                "Your loan for the book '" + bookTitle + "' has been successfully completed.\n" +
                "Please return the book by " + returnDate + ".\n\n" +
                "Thank you for using our library services.\n\n" +
                "Best regards,\n" +
                "Library Team";

        sendMail(toEmail, subject, body);
    }

    // Gửi mail trước ngày trả sách 1 ngày nếu chưa trả
    public void sendReturnReminderMail(String toEmail, String bookTitle, LocalDateTime returnDate) {
        String subject = "Return Reminder Notification";
        String body = "Dear User,\n\n" +
                "This is a reminder that the book '" + bookTitle + "' is due for return on " + returnDate + ".\n" +
                "Please ensure to return it on time to avoid any late fees.\n\n" +
                "Thank you for your cooperation.\n\n" +
                "Best regards,\n" +
                "Library Team";

        sendMail(toEmail, subject, body);
    }

    private void sendMail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("web8386@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        System.out.println("Email sent to: " + toEmail);
    }

}
