package org.crue.hercules.sgi.tp.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bean_method_cron_tasks")
@DiscriminatorValue("CRON")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeanMethodCronTask extends BeanMethodTask {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  public static final int CRON_EXPRESSION_LENGTH = 256;

  /** Cron expression */
  @Column(name = "cron_expression", length = CRON_EXPRESSION_LENGTH, nullable = false)
  @NotEmpty
  @Size(max = CRON_EXPRESSION_LENGTH)
  private String cronExpression;
}