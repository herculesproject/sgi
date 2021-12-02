package org.crue.hercules.sgi.com.service;

import java.security.Security;
import java.util.Arrays;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.MailFolder;
import com.icegreen.greenmail.store.StoredMessage;
import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.ServerSetupTest;

import org.apache.commons.mail.util.MimeMessageParser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = { "spring.mail.host=127.0.0.1",
    "spring.mail.port=3465", // default protocol port + 3000 as offset
    "spring.mail.protocol=smtps",
    "spring.mail.username=" + EmailServiceTest.SENDER_NAME,
    "spring.mail.password=" + EmailServiceTest.SENDER_PASSWORD,
    "spring.mail.properties.mail.smtp.auth=true",
    "spring.mail.properties.mail.smtp.startttls.enabled=true",
    "spring.mail.properties.mail.from.email=" + EmailServiceTest.SENDER_EMAIL,
    "spring.mail.properties.mail.from.name=" + EmailServiceTest.SENDER_NAME,
    "spring.mail.properties.mail.from.copy=true" })
@Import({ MailSenderAutoConfiguration.class, EmailService.class })
class EmailServiceTest extends BaseServiceTest {
  public static final String SENDER_EMAIL = "from@demo.local";
  public static final String SENDER_NAME = "Sender";
  public static final String SENDER_PASSWORD = "password";
  public static final String RECEIVER_EMAIL = "to@demo.local";
  public static final String RECEIVER_NAME = "Target";
  public static final String RECEIVER_PASSWORD = "password";

  static {
    // Avoid exception:
    // javax.net.ssl.SSLHandshakeException:
    // PKIX path building failed:
    // sun.security.provider.certpath.SunCertPathBuilderException:
    // unable to find valid certification path to requested target
    Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
  }

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTPS)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser(
          SENDER_EMAIL,
          SENDER_NAME,
          SENDER_PASSWORD)
          .withUser(
              RECEIVER_EMAIL,
              RECEIVER_NAME,
              RECEIVER_PASSWORD))
      .withPerMethodLifecycle(false);

  @Autowired
  private EmailService service;

  @Test
  void sendSimpleMessageTest() throws Exception {
    // given: A new message to be sent
    InternetAddress sender = new InternetAddress(SENDER_EMAIL, SENDER_NAME);
    InternetAddress receiver = new InternetAddress(RECEIVER_EMAIL, RECEIVER_NAME);
    List<InternetAddress> receivers = Arrays.asList(receiver);
    String subject = "Subject";
    String textBody = "Hello World!";
    String htmlBody = "<b class=\"color:red\">Hello World!</b>";

    // when: The message is sent
    service.sendMessage(receivers, subject, textBody, htmlBody);

    // then: Two message are received
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    Assertions.assertThat(receivedMessages).hasSize(2);
    // they are copies of same message
    Assertions.assertThat(receivedMessages[0].getSubject()).isEqualTo(receivedMessages[1].getSubject());
    MimeMessageParser parser0 = new MimeMessageParser(receivedMessages[0]).parse();
    MimeMessageParser parser1 = new MimeMessageParser(receivedMessages[1]).parse();
    Assertions.assertThat(parser0.getPlainContent()).isEqualTo(parser1.getPlainContent());
    Assertions.assertThat(parser0.getHtmlContent()).isEqualTo(parser1.getHtmlContent());
    Assertions.assertThat(receivedMessages[0].getAllRecipients()).isEqualTo(receivedMessages[1].getAllRecipients());
    // "to" is mpty
    Address[] tos = receivedMessages[0].getRecipients(RecipientType.TO);
    Assertions.assertThat(tos).isNullOrEmpty();
    // sender is in "cc" (receiver is in "bcc")
    Address[] ccs = receivedMessages[0].getRecipients(RecipientType.CC);
    Assertions.assertThat(ccs).hasSize(1);
    Assertions.assertThat(ccs[0].toString()).hasToString(sender.toString());
    // one and only one message is in the receiver inbox
    GreenMailUser targetUser = greenMail.setUser(RECEIVER_NAME, RECEIVER_PASSWORD);
    MailFolder targetInbox = greenMail.getManagers().getImapHostManager().getInbox(targetUser);
    List<StoredMessage> targetMessages = targetInbox.getMessages();
    Assertions.assertThat(targetMessages.size()).isEqualTo(1);
    // and the message is a copy of the sent message
    MimeMessageParser targetMessageParser = new MimeMessageParser(targetMessages.get(0).getMimeMessage()).parse();
    Assertions.assertThat(targetMessageParser.getPlainContent()).isEqualTo(textBody);
    Assertions.assertThat(targetMessageParser.getHtmlContent()).isEqualTo(htmlBody);
    // one and only one message is in the sender inbox
    GreenMailUser senderUser = greenMail.setUser(SENDER_NAME, SENDER_PASSWORD);
    MailFolder senderInbox = greenMail.getManagers().getImapHostManager().getInbox(senderUser);
    List<StoredMessage> senderMessages = senderInbox.getMessages();
    Assertions.assertThat(senderMessages.size()).isEqualTo(1);
    // and the message is a copy of the sent message
    MimeMessageParser senderMessageParser = new MimeMessageParser(senderMessages.get(0).getMimeMessage()).parse();
    Assertions.assertThat(senderMessageParser.getPlainContent()).isEqualTo(textBody);
    Assertions.assertThat(senderMessageParser.getHtmlContent()).isEqualTo(htmlBody);
  }

}
