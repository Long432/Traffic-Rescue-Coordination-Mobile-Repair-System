package com.sosrescuenow.application.port;

import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.TrangThaiCuoc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Cong ra: luu tru cuoc cuu ho. Cac ham tra ve boolean la cap nhat CO DIEU KIEN
 * (chi thanh cong neu cuoc dang o dung trang thai mong doi), nguyen tu o tang CSDL;
 * false nghia la nguoi khac vua thay doi cuoc.
 */
public interface CuocCuuHoRepository {

    CuocCuuHo luu(CuocCuuHo cuocMoi);

    Optional<CuocCuuHo> timTheoId(Long id);

    List<CuocCuuHo> timCuaKhach(Integer khachId);

    /** So cuoc cua khach chua ket thuc (tu DANG_TIM den CHO_THANH_TOAN). */
    long demDangHoatDong(Integer khachId);

    /** Cuoc DANG_TIM co thoi diem bat dau tim SAU {@code moc}. */
    List<CuocCuuHo> timDangTimBatDauSau(LocalDateTime moc);

    /** Cuoc DANG_TIM co thoi diem bat dau tim TRUOC HOAC BANG {@code moc}. */
    List<CuocCuuHo> timDangTimBatDauTruoc(LocalDateTime moc);

    /** Cuoc tho dang lam (DA_NHAN den CHO_THANH_TOAN), moi nhat truoc; rong neu tho dang ranh. */
    Optional<CuocCuuHo> timCuocDangLamCuaTho(Integer thoId);

    /** DANG_TIM -> DA_NHAN va gan tho. */
    boolean nhanCuoc(Long cuocId, Integer thoId);

    /** Tho tra cuoc: (tu) -> DANG_TIM, bo tho, dat lai moc bat dau tim. Chi tho dang giu cuoc moi thanh cong. */
    boolean thoTraCuoc(Long cuocId, Integer thoId, TrangThaiCuoc tuTrangThai, LocalDateTime batDauTimLaiLuc);

    boolean chuyenTrangThai(Long cuocId, TrangThaiCuoc tu, TrangThaiCuoc den);

    /** DANG_SUA -> CHO_THANH_TOAN kem tong tien. */
    boolean chuyenSangChoThanhToan(Long cuocId, BigDecimal tongTien);

    /** (tu) -> HUY kem ly do. */
    boolean huyCuoc(Long cuocId, TrangThaiCuoc tuTrangThai, String lyDo);
}
