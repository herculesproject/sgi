package org.crue.hercules.sgdoc.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Pageable con informacion de ordenacion pero sin paginacion.
 */
public class UnpagedSorted implements Pageable {
  private Sort sort;

  public UnpagedSorted(Sort sort) {
    this.sort = sort;
  }

  @Override
  public boolean isPaged() {
    return false;
  }

  @Override
  public Pageable previousOrFirst() {
    return this;
  }

  @Override
  public Pageable next() {
    return this;
  }

  @Override
  public boolean hasPrevious() {
    return false;
  }

  @Override
  public Sort getSort() {
    return sort;
  }

  @Override
  public int getPageSize() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getPageNumber() {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getOffset() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Pageable first() {
    return this;
  }

}
