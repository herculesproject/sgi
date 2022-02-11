package org.crue.hercules.sgi.com.freemarker;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.Instant;
import java.util.Optional;

import org.crue.hercules.sgi.com.model.EmailTpl;
import org.crue.hercules.sgi.com.repository.EmailTplRepository;
import org.crue.hercules.sgi.framework.problem.message.ProblemMessage;
import org.springframework.util.Assert;

import freemarker.cache.TemplateLoader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link TemplateLoader} that supports loading templates for the three
 * different parts of an email:
 * <ul>
 * <li>TEMPLATENAME://subject: for the template subject</li>
 * <li>TEMPLATENAME://content/text: for the template text content</li>
 * <li>TEMPLATENAME://content/html: for the template html content</li>
 * </ul>
 */
@Slf4j
public class FreemarkerDatabaseEmailTemplateLoader implements TemplateLoader {
  public static final String TEMPLATE_NOT_FOUND_MESSAGE = "NotFound: %s";
  public static final String PATH_PREFIX = "://";
  public static final String PATH_SUBJECT = PATH_PREFIX + "subject";
  public static final String PATH_CONTENT_TEXT = PATH_PREFIX + "content/text";
  public static final String PATH_CONTENT_HTML = PATH_PREFIX + "content/html";

  private EmailTplRepository repository;

  public FreemarkerDatabaseEmailTemplateLoader(EmailTplRepository repository) {
    log.debug(
        "FreemarkerDatabaseEmailTemplateLoader(EmailTplRepository repository) - start");
    this.repository = repository;
    log.debug(
        "FreemarkerDatabaseEmailTemplateLoader(EmailTplRepository repository) - end");
  }

  @Override
  public void closeTemplateSource(Object name) throws IOException {
    // Do nothing
  }

  @Override
  public Object findTemplateSource(String name) throws IOException {
    log.debug(
        "findTemplateSource(String name) - start");
    Assert.isTrue(name.contains(PATH_PREFIX),
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder().key("org.crue.hercules.sgi.com.exceptions.NoTemplatePathException.message")
            .build());
    String emailTplName = getTplName(name);
    String tplPath = getTplPath(name);
    switch (tplPath) {
      case PATH_SUBJECT:
      case PATH_CONTENT_TEXT:
      case PATH_CONTENT_HTML:
        break;
      default:
        return null;
    }

    Optional<EmailTpl> emailTpl = repository.findByName(
        emailTplName);
    EmailTplTemplateSource returnValue = null;
    if (emailTpl.isPresent()) {
      returnValue = new EmailTplTemplateSource(emailTplName, tplPath, emailTpl.get());
    }
    log.debug(
        "findTemplateSource(String name) - end");
    return returnValue;
  }

  @Override
  public long getLastModified(Object templateSource) {
    log.debug(
        "getLastModified(Object templateSource) - start");
    Assert.isTrue(
        templateSource instanceof EmailTplTemplateSource,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder()
            .key("org.crue.hercules.sgi.com.exceptions.NoEmailTplTemplateSourceInstance.message")
            .build());
    EmailTplTemplateSource emailTplTemplateSource = (EmailTplTemplateSource) templateSource;
    Instant lastModified = emailTplTemplateSource.getEmailTpl().getLastModifiedDate();
    if (lastModified == null) {
      log.debug(
          "getLastModified(Object templateSource) - end");
      return -1;
    }
    long returnValue = lastModified.toEpochMilli();
    log.debug(
        "getLastModified(Object templateSource) - end");
    return returnValue;
  }

  @Override
  public Reader getReader(Object templateSource, String encoding) throws IOException {
    log.debug(
        "getReader(Object templateSource, String encoding) - start");
    Assert.isTrue(
        templateSource instanceof EmailTplTemplateSource,
        // Defer message resolution untill is needed
        () -> ProblemMessage.builder()
            .key("org.crue.hercules.sgi.com.exceptions.NoEmailTplTemplateSourceInstance.message")
            .build());
    EmailTplTemplateSource emailTplTemplateSource = (EmailTplTemplateSource) templateSource;
    Reader returnValue = null;
    switch (emailTplTemplateSource.getPath()) {
      case PATH_SUBJECT:
        if (emailTplTemplateSource.getEmailTpl() != null && emailTplTemplateSource.getEmailTpl().getSubjectTpl() != null
            && emailTplTemplateSource
                .getEmailTpl().getSubjectTpl().getTpl() != null) {
          returnValue = new StringReader(emailTplTemplateSource.getEmailTpl().getSubjectTpl().getTpl());
        } else {
          log.warn(String.format(TEMPLATE_NOT_FOUND_MESSAGE,
              emailTplTemplateSource.getName() + emailTplTemplateSource.getPath()));
        }
        break;
      case PATH_CONTENT_TEXT:
        if (emailTplTemplateSource.getEmailTpl() != null && emailTplTemplateSource.getEmailTpl().getSubjectTpl() != null
            && emailTplTemplateSource
                .getEmailTpl().getContentTpl().getTplText() != null) {
          returnValue = new StringReader(emailTplTemplateSource.getEmailTpl().getContentTpl().getTplText());
        } else {
          log.warn(String.format(TEMPLATE_NOT_FOUND_MESSAGE,
              emailTplTemplateSource.getName() + emailTplTemplateSource.getPath()));
        }
        break;
      case PATH_CONTENT_HTML:
        if (emailTplTemplateSource.getEmailTpl() != null && emailTplTemplateSource.getEmailTpl().getSubjectTpl() != null
            && emailTplTemplateSource
                .getEmailTpl().getContentTpl().getTplHtml() != null) {
          returnValue = new StringReader(emailTplTemplateSource.getEmailTpl().getContentTpl().getTplHtml());
        } else {
          log.warn(String.format(TEMPLATE_NOT_FOUND_MESSAGE,
              emailTplTemplateSource.getName() + emailTplTemplateSource.getPath()));
        }
        break;
    }
    log.debug(
        "getReader(Object templateSource, String encoding) - end");
    return returnValue;
  }

  private String getTplName(String name) {
    return name.substring(0, name.indexOf(PATH_PREFIX));
  }

  private String getTplPath(String name) {
    return name.substring(name.indexOf(PATH_PREFIX));
  }

  @Data
  @EqualsAndHashCode(callSuper = false)
  @NoArgsConstructor
  @AllArgsConstructor
  public static class EmailTplTemplateSource {
    private String name;
    private String path;
    private EmailTpl emailTpl;
  }
}
