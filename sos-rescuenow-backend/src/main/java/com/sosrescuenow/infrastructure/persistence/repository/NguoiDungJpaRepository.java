package com.sosrescuenow.infrastructure.persistence.repository;

import com.sosrescuenow.infrastructure.persistence.entity.NguoiDungEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NguoiDungJpaRepository extends JpaRepository<NguoiDungEntity, Integer> {

    Optional<NguoiDungEntity> findBySoDienThoai(String soDienThoai);

    boolean existsBySoDienThoai(String soDienThoai);
}
