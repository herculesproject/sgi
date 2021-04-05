package org.crue.hercules.sgi.csp.repository.predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.crue.hercules.sgi.csp.model.Programa;
import org.crue.hercules.sgi.csp.model.Proyecto;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante;
import org.crue.hercules.sgi.csp.model.ProyectoEntidadConvocante_;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo;
import org.crue.hercules.sgi.csp.model.ProyectoEquipo_;
import org.crue.hercules.sgi.csp.model.Proyecto_;
import org.crue.hercules.sgi.csp.model.RolProyecto_;
import org.crue.hercules.sgi.csp.repository.ProgramaRepository;
import org.crue.hercules.sgi.framework.rsql.SgiRSQLPredicateResolver;
import org.springframework.util.CollectionUtils;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.RSQLOperators;

public class ProyectoPredicateResolver implements SgiRSQLPredicateResolver<Proyecto> {
  private enum Property {
    /* */
    PLAN_INVESTIGACION("planInvestigacion"),
    /* */
    RESPONSABLE_PROYECTO("responsableProyecto");

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

  private final ProgramaRepository programaRepository;

  private ProyectoPredicateResolver(ProgramaRepository programaRepository) {
    this.programaRepository = programaRepository;
  }

  public static ProyectoPredicateResolver getInstance(ProgramaRepository programaRepository) {
    return new ProyectoPredicateResolver(programaRepository);
  }

  private Predicate buildByPlanInvestigacion(ComparisonNode node, Root<Proyecto> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }

    List<Programa> programasQuery = new ArrayList<Programa>();
    List<Programa> programasHijos = new ArrayList<Programa>();
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

    ListJoin<Proyecto, ProyectoEntidadConvocante> joinEntidadesConvocantes = root.join(Proyecto_.entidadesConvocantes,
        JoinType.LEFT);

    return cb.or(joinEntidadesConvocantes.get(ProyectoEntidadConvocante_.programa).in(programasQuery),
        joinEntidadesConvocantes.get(ProyectoEntidadConvocante_.programaConvocatoria).in(programasQuery));
  }

  private Predicate buildByResponsableEquipo(ComparisonNode node, Root<Proyecto> root, CriteriaQuery<?> query,
      CriteriaBuilder cb) {
    ComparisonOperator operator = node.getOperator();
    if (!operator.equals(RSQLOperators.EQUAL)) {
      // Unsupported Operator
      throw new IllegalArgumentException("Unsupported operator: " + operator + " for " + node.getSelector());
    }
    if (node.getArguments().size() != 1) {
      // Bad number of arguments
      throw new IllegalArgumentException("Bad number of arguments for " + node.getSelector());
    }

    String personaRef = node.getArguments().get(0);
    ListJoin<Proyecto, ProyectoEquipo> joinEquipos = root.join(Proyecto_.equipo, JoinType.LEFT);

    return cb.and(cb.equal(joinEquipos.get(ProyectoEquipo_.personaRef), personaRef),
        cb.equal(joinEquipos.get(ProyectoEquipo_.rolProyecto).get(RolProyecto_.rolPrincipal), true));
  }

  @Override
  public boolean isManaged(ComparisonNode node) {
    Property property = Property.fromCode(node.getSelector());
    return property != null;
  }

  @Override
  public Predicate toPredicate(ComparisonNode node, Root<Proyecto> root, CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder) {
    switch (Property.fromCode(node.getSelector())) {
    case PLAN_INVESTIGACION:
      return buildByPlanInvestigacion(node, root, query, criteriaBuilder);
    case RESPONSABLE_PROYECTO:
      return buildByResponsableEquipo(node, root, query, criteriaBuilder);
    default:
      return null;
    }
  }
}
