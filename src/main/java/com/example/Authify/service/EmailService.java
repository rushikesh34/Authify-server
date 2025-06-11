package com.example.Authify.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
	
	private final JavaMailSender mailSender;
	
	@Value("${spring.mail.properties.mail.smtp.from}")
	private String fromEmail;
//	 @Autowired
//	  private TemplateEngine templateEngine;
//	
	
	public void sendWelcomeEmail(String toEmail,String name) {
		SimpleMailMessage mail=new SimpleMailMessage();
		mail.setFrom(fromEmail);
		mail.setTo(toEmail);
		mail.setSubject("Welcome to our platform");
		mail.setText("hello"+" "+name+",\n\n thanks for registering with us ! \n\n Regards,\nAuthify Team");
		mailSender.send(mail);
		
	}
	
//	public void sendOtpEmail(String to, String otp, String type) {
//        Context context = new Context();
//        context.setVariable("otp", otp);
//
//        String templateName = type.equals("reset") ? "reset-password" : "verify-email";
//        String subject = type.equals("reset") ? "Password Reset OTP" : "Email Verification OTP";
//
//        String body = templateEngine.process(templateName, context);
//
//        MimeMessage message = mailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(body, true);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send email", e);
//        }
//    }
	
	public void sendResetOtpEmail(String toEmail,String otp) {
		SimpleMailMessage mail=new SimpleMailMessage();
		mail.setFrom(fromEmail);
		mail.setTo(toEmail);
		mail.setSubject("Password Reset Otp");
		mail.setText("Your otp for reseting your password is "+otp+". Use this otp to proceed with reseting your password");
		mailSender.send(mail);
	}
	
	public void sendVerifyOtpToEmail(String toEmail,String otp) {
		SimpleMailMessage mail=new SimpleMailMessage();
		mail.setFrom(fromEmail);
		mail.setTo(toEmail);
		mail.setSubject("Account varification Otp");
		mail.setText("Your otp for Account varifiaction is "+otp+".  usen this Otp to proceed with verify the account. \n\n Regards,\nAuthify Team");
		mailSender.send(mail);
		
	}
	
	

}
