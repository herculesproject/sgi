package org.crue.hercules.sgi.com.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private JavaMailSender emailSender;
  private InternetAddress from;

  public EmailService(@Autowired JavaMailSender emailSender,
      @Value("${spring.mail.properties.mail.from.email}") String fromEmail,
      @Value("${spring.mail.properties.mail.from.name}") String fromName) throws AddressException {
    this.emailSender = emailSender;
    try {
      from = new InternetAddress(fromEmail, fromName);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      from = new InternetAddress(fromEmail);
    }
  }

  public void sendMessage(List<InternetAddress> internetAddresses, String subject, String textBody,
      String htmlBody)
      throws UnsupportedEncodingException, MessagingException {
    MimeMessage message = emailSender.createMimeMessage();

    message.setSubject(subject);
    message.setFrom(from);
    message.addRecipient(Message.RecipientType.TO, from);

    for (InternetAddress internetAddress : internetAddresses) {
      message.addRecipient(Message.RecipientType.BCC, internetAddress);
    }

    Multipart multipart = new MimeMultipart();

    // PLAIN TEXT
    if (StringUtils.isNotEmpty(textBody)) {
      BodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setText(textBody);
      multipart.addBodyPart(messageBodyPart);
    }

    // HTML TEXT
    if (StringUtils.isNotEmpty(htmlBody)) {
      BodyPart messageBodyPart = new MimeBodyPart();
      messageBodyPart.setContent(htmlBody, MediaType.TEXT_HTML_VALUE);
      multipart.addBodyPart(messageBodyPart);
    }

    message.setContent(multipart);

    emailSender.send(message);
  }
}
