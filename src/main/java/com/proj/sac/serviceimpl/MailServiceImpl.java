package com.proj.sac.serviceimpl;

import com.proj.sac.util.MessageStructure;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl {

    private final JavaMailSender javaMailSender;

    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendMail(MessageStructure message) throws MessagingException
    {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);

        helper.setTo(message.getTo());
        helper.setSubject(message.getSubject());
        helper.setSentDate(message.getSentDate());
        helper.setText(message.getText(), true);

        javaMailSender.send(mimeMessage);
    }
}
