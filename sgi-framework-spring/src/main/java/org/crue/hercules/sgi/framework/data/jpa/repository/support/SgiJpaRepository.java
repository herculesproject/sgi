package org.crue.hercules.sgi.framework.data.jpa.repository.support;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SgiJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> {

  private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

  public SgiJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
  }

  public SgiJpaRepository(Class<T> domainClass, EntityManager em) {
    super(domainClass, em);
  }

  @Override
  public void setEscapeCharacter(EscapeCharacter escapeCharacter) {
    this.escapeCharacter = escapeCharacter;
    super.setEscapeCharacter(escapeCharacter);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.data.repository.PagingAndSortingRepository#findAll(org.
   * springframework.data.domain.Pageable)
   */
  @Override
  public Page<T> findAll(Pageable pageable) {

    if (isUnpagedAndUnsorted(pageable)) {
      return new PageImpl<T>(findAll());
    }

    return findAll((Specification<T>) null, pageable);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.data.jpa.repository.JpaSpecificationExecutor#findAll(org.
   * springframework.data.jpa.domain.Specification,
   * org.springframework.data.domain.Pageable)
   */
  @Override
  public Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable) {

    TypedQuery<T> query = getQuery(spec, pageable);
    return isUnpagedAndUnsorted(pageable) ? new PageImpl<T>(query.getResultList())
        : readPage(query, getDomainClass(), pageable, spec);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.data.repository.query.QueryByExampleExecutor#findAll(org.
   * springframework.data.domain.Example,
   * org.springframework.data.domain.Pageable)
   */
  @Override
  public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {

    ExampleSpecification<S> spec = new ExampleSpecification<>(example, escapeCharacter);
    Class<S> probeType = example.getProbeType();
    TypedQuery<S> query = getQuery(new ExampleSpecification<>(example, escapeCharacter), probeType, pageable);

    return isUnpagedAndUnsorted(pageable) ? new PageImpl<>(query.getResultList())
        : readPage(query, probeType, pageable, spec);
  }

  /**
   * Creates a new {@link TypedQuery} from the given {@link Specification}.
   *
   * @param spec     can be {@literal null}.
   * @param pageable must not be {@literal null}.
   * @return
   */
  protected TypedQuery<T> getQuery(@Nullable Specification<T> spec, Pageable pageable) {

    Sort sort = pageable.getSort();
    return getQuery(spec, getDomainClass(), sort);
  }

  /**
   * Creates a new {@link TypedQuery} from the given {@link Specification}.
   *
   * @param spec        can be {@literal null}.
   * @param domainClass must not be {@literal null}.
   * @param pageable    must not be {@literal null}.
   * @return
   */
  protected <S extends T> TypedQuery<S> getQuery(@Nullable Specification<S> spec, Class<S> domainClass,
      Pageable pageable) {

    Sort sort = pageable.getSort();
    return getQuery(spec, domainClass, sort);
  }

  private static boolean isUnpagedAndUnsorted(Pageable pageable) {
    return pageable.isUnpaged() && !pageable.getSort().isSorted();
  }

  /**
   * {@link Specification} that gives access to the {@link Predicate} instance
   * representing the values contained in the {@link Example}.
   *
   * @author Christoph Strobl
   * @since 1.10
   * @param <T>
   */
  private static class ExampleSpecification<T> implements Specification<T> {

    private static final long serialVersionUID = 1L;

    private final Example<T> example;
    private final EscapeCharacter escapeCharacter;

    /**
     * Creates new {@link ExampleSpecification}.
     *
     * @param example
     * @param escapeCharacter
     */
    ExampleSpecification(Example<T> example, EscapeCharacter escapeCharacter) {

      Assert.notNull(example, "Example must not be null!");
      Assert.notNull(escapeCharacter, "EscapeCharacter must not be null!");

      this.example = example;
      this.escapeCharacter = escapeCharacter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.data.jpa.domain.Specification#toPredicate(javax.
     * persistence.criteria.Root, javax.persistence.criteria.CriteriaQuery,
     * javax.persistence.criteria.CriteriaBuilder)
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
      return QueryByExamplePredicateBuilder.getPredicate(root, cb, example, escapeCharacter);
    }
  }
}