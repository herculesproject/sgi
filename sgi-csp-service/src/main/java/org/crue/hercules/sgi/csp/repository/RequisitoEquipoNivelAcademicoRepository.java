package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.RequisitoEquipoNivelAcademico;
import org.crue.hercules.sgi.csp.repository.custom.CustomRequisitoEquipoNivelAcademicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequisitoEquipoNivelAcademicoRepository extends JpaRepository<RequisitoEquipoNivelAcademico, Long>,
    JpaSpecificationExecutor<RequisitoEquipoNivelAcademico>, CustomRequisitoEquipoNivelAcademicoRepository {
}
