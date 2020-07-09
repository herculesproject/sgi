package org.crue.hercules.sgi.framework.web.config;

import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiJpaRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(repositoryFactoryBeanClass = SgiJpaRepositoryFactoryBean.class)
public class SgiDataConfig {

}