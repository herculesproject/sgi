package org.crue.hercules.sgi.framework.data.jpa.domain;

import java.time.Instant;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {
  @CreatedBy
  protected String createdBy;

  @CreatedDate
  protected Instant creationDate;

  @LastModifiedBy
  protected String lastModifiedBy;

  @LastModifiedDate
  protected Instant lastModifiedDate;
}
