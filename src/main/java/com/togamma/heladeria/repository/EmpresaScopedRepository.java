package com.togamma.heladeria.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.togamma.heladeria.model.BaseEntity;

@NoRepositoryBean
public interface EmpresaScopedRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    Page<T> findByEmpresaId(Long idEmpresa, Pageable pageable);
    Optional<T> findByIdAndEmpresaId(Long id, Long idEmpresa);
    Boolean existsByIdAndEmpresaId(Long id, Long idEmpresa);
}
