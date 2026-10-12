package com.sosrescuenow.infrastructure.persistence.repository;

import com.sosrescuenow.infrastructure.persistence.entity.DanhGiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DanhGiaJpaRepository extends JpaRepository<DanhGiaEntity, Long> {

    Optional<DanhGiaEntity> findByCuocId(Long cuocId);

    /** So sao tat ca cac danh gia cho cac cuoc ma tho nay da phu trach. */
    @Query("select d.soSao from DanhGiaEntity d, CuocCuuHoEntity c where c.id = d.cuocId and c.thoId = :thoId")
    List<Integer> soSaoCuaTho(@Param("thoId") Integer thoId);
}
