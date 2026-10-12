package com.sosrescuenow.infrastructure.persistence.adapter;

import com.sosrescuenow.application.port.CuocCuuHoRepository;
import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.TrangThaiCuoc;
import com.sosrescuenow.infrastructure.persistence.entity.CuocCuuHoEntity;
import com.sosrescuenow.infrastructure.persistence.repository.CuocCuuHoJpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
class CuocCuuHoRepositoryAdapter implements CuocCuuHoRepository {

    /** Cac trang thai chua ket thuc. */
    private static final List<TrangThaiCuoc> TRANG_THAI_HOAT_DONG = Arrays.stream(TrangThaiCuoc.values())
            .filter(t -> !t.laKetThuc())
            .toList();

    /** Cac trang thai thuoc ve mot tho dang lam viec (da nhan, chua ket thuc). */
    private static final List<TrangThaiCuoc> TRANG_THAI_THO_DANG_LAM = List.of(
            TrangThaiCuoc.DA_NHAN, TrangThaiCuoc.DANG_DEN, TrangThaiCuoc.DA_DEN,
            TrangThaiCuoc.DANG_SUA, TrangThaiCuoc.CHO_THANH_TOAN);

    private final CuocCuuHoJpaRepository jpa;

    CuocCuuHoRepositoryAdapter(CuocCuuHoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public CuocCuuHo luu(CuocCuuHo c) {
        CuocCuuHoEntity e = new CuocCuuHoEntity();
        e.setId(c.getId());
        e.setKhachId(c.getKhachId());
        e.setThoId(c.getThoId());
        e.setLoaiSuCoId(c.getLoaiSuCoId());
        e.setLatitude(ToaDo.sangDecimal(c.getViTri().latitude()));
        e.setLongitude(ToaDo.sangDecimal(c.getViTri().longitude()));
        e.setDiaChi(c.getDiaChi());
        e.setGhiChu(c.getGhiChu());
        e.setTrangThai(c.getTrangThai());
        e.setGiaUocTinh(c.getGiaUocTinh());
        e.setTongTien(c.getTongTien());
        e.setLyDoHuy(c.getLyDoHuy());
        e.setNgayTao(c.getNgayTao());
        e.setBatDauTimLuc(c.getBatDauTimLuc());
        return sangDomain(jpa.save(e));
    }

    @Override
    public Optional<CuocCuuHo> timTheoId(Long id) {
        return jpa.findById(id).map(CuocCuuHoRepositoryAdapter::sangDomain);
    }

    @Override
    public List<CuocCuuHo> timCuaKhach(Integer khachId) {
        return jpa.findTop50ByKhachIdOrderByIdDesc(khachId).stream()
                .map(CuocCuuHoRepositoryAdapter::sangDomain).toList();
    }

    @Override
    public long demDangHoatDong(Integer khachId) {
        return jpa.countByKhachIdAndTrangThaiIn(khachId, TRANG_THAI_HOAT_DONG);
    }

    @Override
    public List<CuocCuuHo> timDangTimBatDauSau(LocalDateTime moc) {
        return jpa.findByTrangThaiAndBatDauTimLucGreaterThan(TrangThaiCuoc.DANG_TIM, moc).stream()
                .map(CuocCuuHoRepositoryAdapter::sangDomain).toList();
    }

    @Override
    public List<CuocCuuHo> timDangTimBatDauTruoc(LocalDateTime moc) {
        return jpa.findByTrangThaiAndBatDauTimLucLessThanEqual(TrangThaiCuoc.DANG_TIM, moc).stream()
                .map(CuocCuuHoRepositoryAdapter::sangDomain).toList();
    }

    @Override
    public Optional<CuocCuuHo> timCuocDangLamCuaTho(Integer thoId) {
        return jpa.findFirstByThoIdAndTrangThaiInOrderByIdDesc(thoId, TRANG_THAI_THO_DANG_LAM)
                .map(CuocCuuHoRepositoryAdapter::sangDomain);
    }

    @Override
    public boolean nhanCuoc(Long cuocId, Integer thoId) {
        return jpa.capNhatThoVaTrangThai(cuocId, thoId, TrangThaiCuoc.DANG_TIM, TrangThaiCuoc.DA_NHAN) == 1;
    }

    @Override
    public boolean thoTraCuoc(Long cuocId, Integer thoId, TrangThaiCuoc tuTrangThai, LocalDateTime batDauTimLaiLuc) {
        return jpa.capNhatTraCuoc(cuocId, thoId, tuTrangThai, TrangThaiCuoc.DANG_TIM, batDauTimLaiLuc) == 1;
    }

    @Override
    public boolean chuyenTrangThai(Long cuocId, TrangThaiCuoc tu, TrangThaiCuoc den) {
        return jpa.capNhatTrangThai(cuocId, tu, den) == 1;
    }

    @Override
    public boolean chuyenSangChoThanhToan(Long cuocId, BigDecimal tongTien) {
        return jpa.capNhatChoThanhToan(cuocId, TrangThaiCuoc.DANG_SUA, TrangThaiCuoc.CHO_THANH_TOAN, tongTien) == 1;
    }

    @Override
    public boolean huyCuoc(Long cuocId, TrangThaiCuoc tuTrangThai, String lyDo) {
        return jpa.capNhatHuy(cuocId, tuTrangThai, TrangThaiCuoc.HUY, lyDo) == 1;
    }

    private static CuocCuuHo sangDomain(CuocCuuHoEntity e) {
        return CuocCuuHo.taiTao(e.getId(), e.getKhachId(), e.getThoId(), e.getLoaiSuCoId(),
                ToaDo.sangViTri(e.getLatitude(), e.getLongitude()), e.getDiaChi(), e.getGhiChu(),
                e.getTrangThai(), e.getGiaUocTinh(), e.getTongTien(), e.getLyDoHuy(),
                e.getNgayTao(), e.getBatDauTimLuc());
    }
}
