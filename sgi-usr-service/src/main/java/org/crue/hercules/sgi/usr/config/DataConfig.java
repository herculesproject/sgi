package org.crue.hercules.sgi.usr.config;

import java.util.stream.Stream;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.crue.hercules.sgi.framework.data.domain.SgiAuditorAware;
import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.DatabaseStartupValidator;
import org.springframework.util.StringUtils;

import liquibase.Liquibase;

/**
 * DataConfig
 * 
 * Jpa configuration.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
    "org.crue.hercules.sgi.usr.repository" }, repositoryBaseClass = SgiJpaRepository.class)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class DataConfig {

  @Bean
  public DatabaseDriver databaseDriver(@Value("${spring.datasource.driver-class-name}") String driverClassName) {
    if (StringUtils.hasLength(driverClassName)) {
      for (DatabaseDriver driver : DatabaseDriver.values()) {
        if (driverClassName.equals(driver.getDriverClassName())) {
          return driver;
        }
      }
    }
    return DatabaseDriver.UNKNOWN;
  }

  @Bean
  public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource, DatabaseDriver databaseDriver) {
    DatabaseStartupValidator dsv = new DatabaseStartupValidator();
    dsv.setDataSource(dataSource);
    dsv.setValidationQuery(databaseDriver.getValidationQuery());
    return dsv;
  }

  @Bean
  public static BeanFactoryPostProcessor dependsOnPostProcessor() {
    return bf -> {
      // Let beans that need the database depend on the DatabaseStartupValidator
      // like the EntityManagerFactory, Liquibase or JdbcTemplate
      String[] jdbc = bf.getBeanNamesForType(JdbcTemplate.class);
      Stream.of(jdbc).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn("databaseStartupValidator"));

      String[] liquibase = bf.getBeanNamesForType(Liquibase.class);
      Stream.of(liquibase).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn("databaseStartupValidator"));

      String[] jpa = bf.getBeanNamesForType(EntityManagerFactory.class);
      Stream.of(jpa).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn("databaseStartupValidator"));
    };
  }

  @Bean
  public AuditorAware<String> auditorAware() {
    return new SgiAuditorAware();
  }
}