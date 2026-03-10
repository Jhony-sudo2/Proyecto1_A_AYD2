package com.ayd2.congress.services.mail;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendText(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }

    @Override
    public void sendCode(String subject, String email,String code) {
        String cuerpo = """
                Hola,

                Este es tu código de recuperacion de contrase;a:

                %s
                """.formatted(code);

        sendText(
                email,
                subject,
                cuerpo);
    }
    

}
