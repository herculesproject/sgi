package org.crue.hercules.sgi.usr.config;

import org.crue.hercules.sgi.framework.data.jpa.repository.support.SimpleJpaRepository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * DataConfig
 * 
 * Jpa configuration.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
    "org.crue.hercules.sgi.usr.repository" }, repositoryBaseClass = SimpleJpaRepository.class)
public class DataConfig {

}