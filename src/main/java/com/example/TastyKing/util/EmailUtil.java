package com.example.TastyKing.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {
    @Autowired
    private JavaMailSender javaMailSender;
    public void sendOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("tuandoiphuyen@gmail.com");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify OTP");
//        mimeMessageHelper.setText("""
//                <div>
//                    <a href = "http://localhost:8080/verify-account?email=%s&otp=%s" target="_blank">Click me to verify</a>
//                </div>
//                """.formatted(email, otp), true);
        mimeMessageHelper.setText("""
                <div>
                    Your OTP is: <strong>%s</strong>
                </div>
                """.formatted(otp), true);
        javaMailSender.send(mimeMessage);

    }
    public void sendOrderConfirmationEmail(String email, String orderId) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("tuandoiphuyen@gmail.com");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Order Confirmation");
        mimeMessageHelper.setText(String.format("""
                <div>
                    Tasty King restaurant</br>
                    Your order has been confirmed. Order ID: <strong>%s</strong>

                    Please arrive to receive your table 15 minutes in advance. After 15 minutes from the date of booking, if you do not show up, your order will be canceled.
                    If you have any questions, please contact: 0386656642
                    Thank you
                    
                </div>
                """, orderId), true);
        javaMailSender.send(mimeMessage);
    }

    public void sendOrderCancelEmail(String email, String orderId) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("tuandoiphuyen@gmail.com");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Order Cancel");
        mimeMessageHelper.setText(String.format("""
                <div>
                    Tasty King restaurant</br>
                    
                    Your order has been canceled. Order ID: <strong>%s</strong>
                    We will refund you within 24 hours of receiving this email.
                    We will contact you as soon as possible. We are so sorry for the inconvenience caused!
                    If you have any questions,feel free to ask, please contact: +84386656642
                    Sincerely!
                    
                </div>
                """, orderId), true);
        javaMailSender.send(mimeMessage);
    }
    public void sendRequestOrderCancelFromAdminEmail(String email, String orderId) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("tuandoiphuyen@gmail.com");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Order Cancel");
        mimeMessageHelper.setText(String.format("""
                <div>
                    Tasty King restaurant</br>
                    Your order has not been accepted. You need to fill in your personal information so we can refund you. Order ID: <strong>%s</strong>

                   If you have already paid. Please contact the restaurant to receive a refund
                    If you have any questions, please contact: 0386656642
                    Thank you
                    
                </div>
                """, orderId), true);
        javaMailSender.send(mimeMessage);
    }

    public void sendActiveAccount(String email, String password) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom("tuandoiphuyen@gmail.com");
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Active account");
        mimeMessageHelper.setText(String.format("""
                <div>
                    Tasty King restaurant</br>
                    Your account has been actived.  Your Password: <strong>%s</strong>

             
                    Thank you
                    
                </div>
                """, password), true);
        javaMailSender.send(mimeMessage);
    }
}
