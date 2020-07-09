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



/**
 * Default implementation of the
 * {@link org.springframework.data.repository.CrudRepository} interface. This
 * will offer you a more sophisticated interface than the plain
 * {@link EntityManager} .
 */
public class SgiJpaRepository<T, ID> extends SimpleJpaRepository<T, ID> {

  private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

  /**
   * Creates a new {@link SgiJpaRepository} to manage objects of the given
   * {@link JpaEntityInformation}.
   *
   * @param entityInformation must not be {@literal null}.
   * @param entityManager     must not be {@literal null}.
   */
  public SgiJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
  }

  /**
   * Creates a new {@link SgiJpaRepository} to manage objects of the given
   * domain type.
   *
   * @param domainClass must not be {@literal null}.
   * @param em          must not be {@literal null}.
   */
  public SgiJpaRepository(Class<T> domainClass, EntityManager em) {
    super(domainClass, em);
  }

  /**
   * Configures the {@link EscapeCharacter} to be used with the repository.
   *
   * @param escapeCharacter Must not be {@literal null}.
   */
  @Override
  public void setEscapeCharacter(EscapeCharacter escapeCharacter) {
    this.escapeCharacter = escapeCharacter;
    super.setEscapeCharacter(escapeCharacter);
  }

  /**
   * Returns a {@link Page} of entities meeting the paging restriction provided in
   * the {@code Pageable} object.
   *
   * @param pageable the Pageable
   * @return a page of entities
   */
  @Override
  public Page<T> findAll(Pageable pageable) {

    if (isUnpagedAndUnsorted(pageable)) {
      return new PageImpl<T>(findAll());
    }

    return findAll((Specification<T>) null, pageable);
  }

  /**
   * Returns a {@link Page} of entities matching the given {@link Specification}.
   *
   * @param spec     can be {@literal null}.
   * @param pageable must not be {@literal null}.
   * @return never {@literal null}.
   */
  @Override
  public Page<T> findAll(@Nullable Specification<T> spec, Pageable pageable) {

    TypedQuery<T> query = getQuery(spec, pageable);
    return isUnpagedAndUnsorted(pageable) ? new PageImpl<T>(query.getResultList())
        : readPage(query, getDomainClass(), pageable, spec);
  }

  /**
   * Returns a {@link Page} of entities matching the given {@link Example}. In
   * case no match could be found, an empty {@link Page} is returned.
   *
   * @param example  must not be {@literal null}.
   * @param pageable can be {@literal null}.
   * @return a {@link Page} of entities matching the given {@link Example}.
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
   * @return TypedQuery
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
   * @return TypedQuery
   */
  protected <S extends T> TypedQuery<S> getQuery(@Nullable Specification<S> spec, Class<S> domainClass,
      Pageable pageable) {

    Sort sort = pageable.getSort();
    return getQuery(spec, domainClass, sort);
  }

  /**
   * @param pageable
   * @return boolean
   */
  private static boolean isUnpagedAndUnsorted(Pageable pageable) {
    return pageable.isUnpaged() && !pageable.getSort().isSorted();
  }

  /**
   * {@link Specification} that gives access to the {@link Predicate} instance
   * representing the values contained in the {@link Example}.
   *
   * @author Christoph Strobl
   * @since 1.10
   * @param <T> the Type
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