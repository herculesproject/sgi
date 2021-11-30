package org.crue.hercules.sgi.com.service;

import java.security.Security;
import java.util.Arrays;
import java.util.List;

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
    "spring.mail.username=from",
    "spring.mail.password=password",
    "spring.mail.properties.mail.smtp.auth=true",
    "spring.mail.properties.mail.smtp.startttls.enabled=true",
    "spring.mail.properties.mail.from.email=from@demo.local",
    "spring.mail.properties.mail.from.name=from" })
@Import({ MailSenderAutoConfiguration.class, EmailService.class })
public class EmailServiceTest extends BaseServiceTest {

  static {
    // Avoid javax.net.ssl.SSLHandshakeException: PKIX path building failed:
    // sun.security.provider.certpath.SunCertPathBuilderException: unable to find
    // valid certification path to requested target
    Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
  }

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTPS)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("from@demo.local", "from", "password")
          .withUser("to@demo.local", "to", "password"))
      .withPerMethodLifecycle(false);

  @Autowired
  private EmailService service;

  @Test
  void sendSimpleMessageTest() throws Exception {
    // given: A new message to be send
    InternetAddress from = new InternetAddress("from@demo.local", "from");
    InternetAddress to = new InternetAddress("to@demo.local", "to");
    List<InternetAddress> recipients = Arrays.asList(to);
    String subject = "Subject";
    String textBody = "Hello World!";
    String htmlBody = "<b class=\"color:red\">Hello World!</b>";

    // when: The message is sent
    service.sendMessage(recipients, subject, textBody, htmlBody);

    // then: The message arrives to the target recipient (in bcc)
    GreenMailUser targetUser = greenMail.setUser("to", "password");
    MailFolder inbox = greenMail.getManagers().getImapHostManager().getInbox(targetUser);
    List<StoredMessage> messages = inbox.getMessages();
    Assertions.assertThat(messages.size()).isEqualTo(1);
    MimeMessage receivedMessage = messages.get(0).getMimeMessage();
    Assertions.assertThat(receivedMessage.getSubject()).isEqualTo(subject);
    MimeMessageParser parser = new MimeMessageParser(receivedMessage).parse();
    String textMessage = parser.getPlainContent();
    Assertions.assertThat(textMessage).isEqualTo(textBody);
    String htmlMessage = parser.getHtmlContent();
    Assertions.assertThat(htmlMessage).isEqualTo(htmlBody);
    // "to" is same as "from" (recipient is in bcc)
    Assertions.assertThat(receivedMessage.getAllRecipients().length).isEqualTo(1);
    Assertions.assertThat(receivedMessage.getAllRecipients()[0].toString()).isEqualTo(from.toString());
  }

}
