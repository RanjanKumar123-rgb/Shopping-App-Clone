//package com.proj.sac.serviceimpl;
//
//import com.proj.sac.util.MessageStructure;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mail.javamail.JavaMailSender;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class MailServiceImplTest {
//
//    @Mock
//    private JavaMailSender javaMailSender;
//
//    @InjectMocks
//    private MailServiceImpl mailService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSendMail_Success() throws MessagingException {
//        MessageStructure message = new MessageStructure();
//        message.setTo("test@example.com");
//        message.setSubject("Test Subject");
//        message.setSentDate(new java.util.Date());
//        message.setText("Test Email Content");
//
//        MimeMessage mimeMessage = mock(MimeMessage.class);
//
//        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
//
//        doNothing().when(javaMailSender).send(any(MimeMessage.class));
//
//        mailService.sendMail(message);
//
//        verify(javaMailSender, times(1)).createMimeMessage();
//        verify(javaMailSender, times(1)).send(mimeMessage);
//    }
//
//    @Test
//    void testSendMail_Exception() {
//        MessageStructure message = new MessageStructure();
//        message.setTo("test@example.com");
//        message.setSubject("Test Subject");
//        message.setSentDate(new java.util.Date());
//        message.setText("Test Email Content");
//
//        MimeMessage mimeMessage = mock(MimeMessage.class);
//
//        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
//        doThrow(new MessagingException("Test Exception")).when(javaMailSender).send(any(MimeMessage.class));
//
//        assertThrows(MessagingException.class, () -> {
//            mailService.sendMail(message);
//        });
//
//        verify(javaMailSender, times(1)).createMimeMessage();
//        verify(javaMailSender, times(1)).send(mimeMessage);
//    }
//}
