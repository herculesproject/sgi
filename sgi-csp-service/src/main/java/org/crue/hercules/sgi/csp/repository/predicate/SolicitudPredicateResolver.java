package org.crue.hercules.sgi.csp.repository.predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Convocatoria;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ConvocatoriaEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.Convocatoria_;
import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Solicitud;
import org.crue.hercules.sgi.csp.model.Solicitud_;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.csp.util.PredicateResolverUtil;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.springframework.util.CollectionUtils;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import io.github.perplexhub.rsql.RSQLOperators;

public class SolicitudPredicateResolver implements SgiRSQLPredicateResolver<Solicitud> {
  private enum Property {
    REFERENCIA_CONVOCATORIA("referenciaConvocatoria"),
    PLAN_INVESTIGACION("planInvestigacion");

    private String code;

    private Property(String code) {
      this.code = code;
    }

    public static Property fromCode(String code) {
      for (Property property : Property.values()) {
        if (property.code.equals(code)) {
          return property;
        }
      }
      return null;
    }
  }

  private static SolicitudPredicateResolver instance;
  private final ProgramaRepository programaRepository;

  private SolicitudPredicateResolver(ProgramaRepository programaRepository) {
    this.programaRepository = programaRepository;
  }

  public static SolicitudPredicateResolver getInstance(ProgramaRepository programaRepository) {
    if (instance == null) {
      instance = new SolicitudPredicateResolver(programaRepository);
    }
    return instance;
  }

  private static Predicate buildByReferenciaConvocatoria(ComparisonNode node, Root<Solicitud> root,
      CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.IGNORE_CASE_LIKE);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    String referenciaConvocatoria = "%" + node.getArguments().get(0) + "%";

    Join<Solicitud, Convocatoria> joinConvocatoria = root.join(Solicitud_.convocatoria, JoinType.LEFT);

    return cb.or(
        cb.and(cb.isNotNull(joinConvocatoria),
            cb.like(joinConvocatoria.get(Convocatoria_.codigo), referenciaConvocatoria)),
        cb.and(cb.and(cb.isNull(joinConvocatoria),
            cb.like(root.get(Solicitud_.convocatoriaExterna), referenciaConvocatoria))));
  }

  private Predicate buildByPlanInvestigacion(ComparisonNode node, Root<Solicitud> root, CriteriaBuilder cb) {
    PredicateResolverUtil.validateOperatorIsSupported(node, RSQLOperators.EQUAL);
    PredicateResolverUtil.validateOperatorArgumentNumber(node, 1);

    List<Programa> programasQuery = new ArrayList<>();
    List<Programa> programasHijos = new ArrayList<>();
    Long idProgramaRaiz = Long.parseLong(node.getArguments().get(0));
    Optional<Programa> programaRaizOpt = this.programaRepository.findById(idProgramaRaiz);
    if (programaRaizOpt.isPresent()) {
      programasQuery.add(programaRaizOpt.get());
      programasHijos.add(programaRaizOpt.get());
    }
    programasHijos = programaRepository.findByPadreIn(programasHijos);
    while (!CollectionUtils.isEmpty(programasHijos)) {
      programasQuery.addAll(programasHijos);
      programasHijos = programaRepository.findByPadreIn(programasHijos);
    }

    Join<Solicitud, Convocatoria> joinConvocatoria = root.join(Solicitud_.convocatoria, JoinType.LEFT);
    ListJoin<Convocatoria, ConvocatoriaEntidadConvocante> joinEntidadesConvocantes = joinConvocatoria
        .join(Convocatoria_.entidadesConvocantes, JoinType.LEFT);

    return cb.or(joinEntidadesConvocantes.get(ConvocatoriaEntidadConvocante_.programa).in(programasQuery),
        joinEntidadesConvocantes.get(ConvocatoriaEntidadConvocante_.programa).in(programasQuery));
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Solicitud> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    Property property = Property.fromCode(node.getSelector());
    if (property == null) {
      return null;
    }

    switch (property) {
      case REFERENCIA_CONVOCATORIA:
        return buildByReferenciaConvocatoria(node, root, criteriaBuilder);
      case PLAN_INVESTIGACION:
        return buildByPlanInvestigacion(node, root, criteriaBuilder);
      default:
        return null;
    }
  }
}
