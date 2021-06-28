package org.crue.hercules.sgi.csp.repository;

import org.crue.hercules.sgi.csp.model.RequisitoIPNivelAcademico;
import org.crue.hercules.sgi.csp.repository.custom.CustomRequisitoIPNivelAcademicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequisitoIPNivelAcademicoRepository extends JpaRepository<RequisitoIPNivelAcademico, Long>,
    JpaSpecificationExecutor<RequisitoIPNivelAcademico>, CustomRequisitoIPNivelAcademicoRepository {
}
