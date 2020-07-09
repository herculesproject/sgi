package org.crue.hercules.sgi.framework.data.jpa.repository.support;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryMethodFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.Nullable;

public class SgiJpaRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
    extends JpaRepositoryFactoryBean<T, S, ID> {

  private EntityPathResolver entityPathResolver;
  private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;
  private JpaQueryMethodFactory queryMethodFactory;

  public SgiJpaRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
    super(repositoryInterface);
  }

  /**
   * Configures the {@link EntityPathResolver} to be used. Will expect a canonical
   * bean to be present but fallback to {@link SimpleEntityPathResolver#INSTANCE}
   * in case none is available.
   *
   * @param resolver must not be {@literal null}.
   */
  @Autowired
  public void setEntityPathResolver(ObjectProvider<EntityPathResolver> resolver) {
    super.setEntityPathResolver(resolver);
    this.entityPathResolver = resolver.getIfAvailable(() -> SimpleEntityPathResolver.INSTANCE);
  }

  /**
   * Configures the {@link JpaQueryMethodFactory} to be used. Will expect a
   * canonical bean to be present but will fallback to
   * {@link org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory}
   * in case none is available.
   *
   * @param factory may be {@literal null}.
   */
  @Autowired
  public void setQueryMethodFactory(@Nullable JpaQueryMethodFactory factory) {
    super.setQueryMethodFactory(factory);
    if (factory != null) {
      this.queryMethodFactory = factory;
    }
  }

  /**
   * Returns a {@link RepositoryFactorySupport}.
   */
  @Override
  protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {

    JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
    jpaRepositoryFactory.setEntityPathResolver(entityPathResolver);
    jpaRepositoryFactory.setEscapeCharacter(escapeCharacter);

    if (queryMethodFactory != null) {
      jpaRepositoryFactory.setQueryMethodFactory(queryMethodFactory);
    }

    return jpaRepositoryFactory;
  }

  public void setEscapeCharacter(char escapeCharacter) {
    super.setEscapeCharacter(escapeCharacter);
    this.escapeCharacter = EscapeCharacter.of(escapeCharacter);
  }
}