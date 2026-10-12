package com.sosrescuenow.infrastructure.persistence.repository;

import com.sosrescuenow.infrastructure.persistence.entity.LoaiSuCoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoaiSuCoJpaRepository extends JpaRepository<LoaiSuCoEntity, Integer> {
}
