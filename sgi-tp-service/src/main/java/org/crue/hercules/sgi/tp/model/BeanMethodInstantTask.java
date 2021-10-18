package org.crue.hercules.sgi.tp.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bean_method_instant_tasks")
@DiscriminatorValue("INSTANT")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeanMethodInstantTask extends BeanMethodTask {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /** Instant */
  @Column(name = "instant", nullable = false)
  @NotEmpty
  private Instant instant;

}