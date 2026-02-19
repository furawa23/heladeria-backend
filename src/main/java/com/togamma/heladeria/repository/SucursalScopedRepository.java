package com.togamma.heladeria.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.togamma.heladeria.model.BaseEntity;

@NoRepositoryBean
public interface SucursalScopedRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    Page<T> findBySucursalId(Long idSucursal, Pageable pageable);
    Boolean existsByIdAndSucursalId(Long id, Long idSucursal);
    Optional<T> findByIdAndSucursalId(Long id, Long idSucursal);
}
