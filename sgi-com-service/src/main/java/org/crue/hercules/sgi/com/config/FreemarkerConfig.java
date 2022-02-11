package org.crue.hercules.sgi.com.config;

import java.util.Properties;

import org.crue.hercules.sgi.com.freemarker.FreemarkerDatabaseEmailTemplateLoader;
import org.crue.hercules.sgi.com.freemarker.FreemarkerEmailTemplateProcessor;
import org.crue.hercules.sgi.com.repository.EmailTplRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Configuration
public class FreemarkerConfig {

  @Bean
  public FreeMarkerConfigurationFactoryBean getFreeMarkerConfiguration(
      FreemarkerDatabaseEmailTemplateLoader freemarkerDatabaseTemplateLoader, SgiConfigProperties sgiConfigProperties) {
    FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
    bean.setPreTemplateLoaders(freemarkerDatabaseTemplateLoader);
    Properties freemarkerProperties = new Properties();
    freemarkerProperties.setProperty(freemarker.template.Configuration.LOCALIZED_LOOKUP_KEY,
        "false");
    freemarkerProperties.setProperty(freemarker.core.Configurable.TIME_ZONE_KEY,
        sgiConfigProperties.getTimeZone().getID());
    bean.setFreemarkerSettings(freemarkerProperties);
    return bean;
  }

  @Bean
  public FreemarkerDatabaseEmailTemplateLoader getFreemarkerDatabaseTemplateLoader(EmailTplRepository repository) {
    return new FreemarkerDatabaseEmailTemplateLoader(repository);
  }

  @Bean
  public FreemarkerEmailTemplateProcessor getFreemarkerEmailTemplateProcessor(EmailTplRepository repository,
      freemarker.template.Configuration freemarkerCfg) {
    return new FreemarkerEmailTemplateProcessor(repository, freemarkerCfg);
  }
}
