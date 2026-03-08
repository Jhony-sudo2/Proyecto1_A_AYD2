package com.ayd2.congress.services.mail;

public interface MailService {
    void sendText(String to,String subject,String text);
    void sendCode(String subject,String email,String code);

}
