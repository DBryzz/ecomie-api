package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.csbf.security.exceptions.BaseException;
import org.csbf.security.exceptions.FailedSendEmailException;
import org.csbf.security.model.User;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.AuthenticationService;
import org.csbf.security.service.EmailService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailServiceImp implements EmailService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepo;
    private final AuthenticationService authService;

    @Async
    @Override
    public void sendEmailVerificationToken(String requestHost, String email) {
        String token = authService.createEmailVerificationToken();
        boolean success = false;
        System.out.println(email);
        System.out.println(userRepo.findByEmail(email).get());
        System.out.println(userRepo.findByEmail(email).isPresent());
        System.out.println(userRepo.findByEmail(email));
        if (userRepo.findByEmail(email).isPresent()) {
            User user = userRepo.findByEmail(email).get();
            user.setEmailVerificationToken(token);
            userRepo.save(user);

            String encodedEmail;
            try {
                encodedEmail = URLEncoder.encode(user.getEmail(), StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                throw new BaseException("Could not encode email");
            }

            String recipient = user.getEmail();
            String subject = "Complete Registration!";
            String body = "To confirm your account, please click here --> " + requestHost +  "/api/v1/auth/confirm-account?email=" + encodedEmail +"&token=" + user.getEmailVerificationToken();

            sendEmail(recipient, subject, body);

        }
    }


    @Async
    @Override
    public void sendEmail(String recipient, String subject, String body, String... from ) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        if(!ArrayUtils.isEmpty(from))
            mailMessage.setFrom(from[0]);
        mailMessage.setTo(recipient);
        mailMessage.setSubject(subject);
        mailMessage.setText(body);
        try {
            javaMailSender.send(mailMessage);
        }catch(MailException e) {
            throw new FailedSendEmailException(e.getMessage());
        }
    }

    @Async
    @Override
    public void sendCustomEmail(String requestHost, String from, String to, String purpose) {
        String encodedToEmail;
        String encodedFromEmail;

        try {
            encodedToEmail = URLEncoder.encode(to, StandardCharsets.UTF_8.toString());
            encodedFromEmail = URLEncoder.encode(from, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new BaseException("Could not encode email");
        }
        String recipient = to;
        String subject = "@ECOMIE - Request To Be " + purpose;
        String body = "I will like to become a/an " + purpose + ": \n\n If you approve of this user, click on the link to approve his/her request " + requestHost +  "/api/v1/secure/admin/update-user-role?email="+encodedFromEmail+"&role="+purpose;
        sendEmail(recipient, subject, body, from);

    }

}
