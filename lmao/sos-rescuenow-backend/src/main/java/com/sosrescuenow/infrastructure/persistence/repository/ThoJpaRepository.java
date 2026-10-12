package com.sosrescuenow.infrastructure.persistence.repository;

import com.sosrescuenow.infrastructure.persistence.entity.ThoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface ThoJpaRepository extends JpaRepository<ThoEntity, Integer> {

    /** Nguyen tu: chi dat ban khi tho dang duyet + online + chua ban. Tra ve so dong cap nhat (0 hoac 1). */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update ThoEntity t set t.dangBan = true "
            + "where t.id = :id and t.dangBan = false and t.trangThaiOnline = true and t.daDuyet = true")
    int datBanNeuRanh(@Param("id") Integer id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update ThoEntity t set t.dangBan = false where t.id = :id")
    int datRanh(@Param("id") Integer id);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update ThoEntity t set t.trangThaiOnline = :online where t.id = :id")
    int datOnline(@Param("id") Integer id, @Param("online") boolean online);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update ThoEntity t set t.diemTrungBinh = :diem where t.id = :id")
    int datDiemTrungBinh(@Param("id") Integer id, @Param("diem") BigDecimal diem);

    @Query("select new com.sosrescuenow.infrastructure.persistence.repository.ThoTomTatRow("
            + "t.id, n.hoTen, n.soDienThoai, t.daDuyet, t.trangThaiOnline, t.dangBan, t.diemTrungBinh) "
            + "from ThoEntity t, NguoiDungEntity n where n.id = t.id order by t.id desc")
    List<ThoTomTatRow> lietKeTatCa();

    @Query("select new com.sosrescuenow.infrastructure.persistence.repository.ThoTomTatRow("
            + "t.id, n.hoTen, n.soDienThoai, t.daDuyet, t.trangThaiOnline, t.dangBan, t.diemTrungBinh) "
            + "from ThoEntity t, NguoiDungEntity n where n.id = t.id and t.daDuyet = false order by t.id desc")
    List<ThoTomTatRow> lietKeChuaDuyet();

    /** Loc tho theo khung toa do (bounding box); khoang cach chinh xac do tang service tinh. */
    @Query("select new com.sosrescuenow.infrastructure.persistence.repository.UngVienRow("
            + "t.id, v.latitude, v.longitude, t.diemTrungBinh, t.tiLeNhanCuoc) "
            + "from ThoEntity t, ViTriThoEntity v "
            + "where v.thoId = t.id "
            + "and t.trangThaiOnline = true and t.dangBan = false and t.daDuyet = true "
            + "and v.latitude between :minLat and :maxLat "
            + "and v.longitude between :minLng and :maxLng "
            + "and v.capNhatLuc >= :viTriTu "
            + "and exists (select l.id from t.loaiSuCoLamDuoc l where l.id = :loaiSuCoId)")
    List<UngVienRow> timUngVien(@Param("minLat") BigDecimal minLat, @Param("maxLat") BigDecimal maxLat,
                                @Param("minLng") BigDecimal minLng, @Param("maxLng") BigDecimal maxLng,
                                @Param("viTriTu") LocalDateTime viTriTu,
                                @Param("loaiSuCoId") Integer loaiSuCoId);
}
