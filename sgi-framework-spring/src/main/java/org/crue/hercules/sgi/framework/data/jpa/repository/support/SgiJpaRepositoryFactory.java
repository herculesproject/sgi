package org.crue.hercules.sgi.framework.data.jpa.repository.support;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;

public class SgiJpaRepositoryFactory extends JpaRepositoryFactory {

  /**
   * Creates a new {@link SgiJpaRepositoryFactory}.
   *
   * @param entityManager must not be {@literal null}
   */
  public SgiJpaRepositoryFactory(EntityManager entityManager) {
    super(entityManager);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.data.repository.core.support.RepositoryFactorySupport#
   * getRepositoryBaseClass(org.springframework.data.repository.core.
   * RepositoryMetadata)
   */
  @Override
  protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
    return SgiJpaRepository.class;
  }

}