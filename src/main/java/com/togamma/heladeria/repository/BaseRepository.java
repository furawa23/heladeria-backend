package com.togamma.heladeria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.togamma.heladeria.model.BaseEntity;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity,ID extends Long> extends JpaRepository<T, ID> {

    List<T> findAllByDeletedAtIsNull();

}
