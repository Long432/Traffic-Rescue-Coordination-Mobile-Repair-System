package com.sosrescuenow.infrastructure.persistence.repository;

import com.sosrescuenow.domain.model.TrangThaiCuoc;
import com.sosrescuenow.infrastructure.persistence.entity.CuocCuuHoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Cac ham "capNhat..." la UPDATE co dieu kien (WHERE TrangThai = :tu) va tra ve so dong bi anh huong:
 * 1 = thanh cong, 0 = cuoc da bi nguoi khac doi trang thai. Day la co che chong tranh chap cua he thong.
 */
public interface CuocCuuHoJpaRepository extends JpaRepository<CuocCuuHoEntity, Long> {

    List<CuocCuuHoEntity> findTop50ByKhachIdOrderByIdDesc(Integer khachId);

    Optional<CuocCuuHoEntity> findFirstByThoIdAndTrangThaiInOrderByIdDesc(Integer thoId, Collection<TrangThaiCuoc> trangThais);

    long countByKhachIdAndTrangThaiIn(Integer khachId, Collection<TrangThaiCuoc> trangThais);

    List<CuocCuuHoEntity> findByTrangThaiAndBatDauTimLucGreaterThan(TrangThaiCuoc trangThai, LocalDateTime moc);

    List<CuocCuuHoEntity> findByTrangThaiAndBatDauTimLucLessThanEqual(TrangThaiCuoc trangThai, LocalDateTime moc);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update CuocCuuHoEntity c set c.thoId = :thoId, c.trangThai = :den "
            + "where c.id = :id and c.trangThai = :tu")
    int capNhatThoVaTrangThai(@Param("id") Long id, @Param("thoId") Integer thoId,
                              @Param("tu") TrangThaiCuoc tu, @Param("den") TrangThaiCuoc den);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update CuocCuuHoEntity c set c.trangThai = :den where c.id = :id and c.trangThai = :tu")
    int capNhatTrangThai(@Param("id") Long id, @Param("tu") TrangThaiCuoc tu, @Param("den") TrangThaiCuoc den);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update CuocCuuHoEntity c set c.trangThai = :den, c.tongTien = :tongTien "
            + "where c.id = :id and c.trangThai = :tu")
    int capNhatChoThanhToan(@Param("id") Long id, @Param("tu") TrangThaiCuoc tu, @Param("den") TrangThaiCuoc den,
                            @Param("tongTien") BigDecimal tongTien);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update CuocCuuHoEntity c set c.trangThai = :den, c.lyDoHuy = :lyDo "
            + "where c.id = :id and c.trangThai = :tu")
    int capNhatHuy(@Param("id") Long id, @Param("tu") TrangThaiCuoc tu, @Param("den") TrangThaiCuoc den,
                   @Param("lyDo") String lyDo);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update CuocCuuHoEntity c set c.thoId = null, c.trangThai = :den, c.batDauTimLuc = :batDau "
            + "where c.id = :id and c.thoId = :thoId and c.trangThai = :tu")
    int capNhatTraCuoc(@Param("id") Long id, @Param("thoId") Integer thoId, @Param("tu") TrangThaiCuoc tu,
                       @Param("den") TrangThaiCuoc den, @Param("batDau") LocalDateTime batDau);
}
