package com.sosrescuenow.infrastructure.persistence.adapter;

import com.sosrescuenow.application.port.HoSoThoRepository;
import com.sosrescuenow.domain.dieuphoi.UngVienTho;
import com.sosrescuenow.domain.model.HoSoTho;
import com.sosrescuenow.domain.model.ThoTomTat;
import com.sosrescuenow.domain.model.ViTri;
import com.sosrescuenow.infrastructure.persistence.entity.LoaiSuCoEntity;
import com.sosrescuenow.infrastructure.persistence.entity.ThoEntity;
import com.sosrescuenow.infrastructure.persistence.repository.LoaiSuCoJpaRepository;
import com.sosrescuenow.infrastructure.persistence.repository.ThoJpaRepository;
import com.sosrescuenow.infrastructure.persistence.repository.ThoTomTatRow;
import com.sosrescuenow.infrastructure.persistence.repository.UngVienRow;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
class HoSoThoRepositoryAdapter implements HoSoThoRepository {

    /** Do dai (km) cua mot do vi do; lay nho hon thuc te (111.2) de khung loc luon du rong. */
    private static final double KM_MOI_DO = 110.0;

    private final ThoJpaRepository jpa;
    private final LoaiSuCoJpaRepository loaiSuCoJpa;

    HoSoThoRepositoryAdapter(ThoJpaRepository jpa, LoaiSuCoJpaRepository loaiSuCoJpa) {
        this.jpa = jpa;
        this.loaiSuCoJpa = loaiSuCoJpa;
    }

    @Override
    public void taoHoSoMoi(Integer nguoiDungId) {
        ThoEntity tho = new ThoEntity(); // gia tri mac dinh nam trong entity: offline, ranh, chua duyet
        tho.setId(nguoiDungId);
        jpa.save(tho);
    }

    @Override
    public Optional<HoSoTho> timHoSo(Integer thoId) {
        // Phai goi trong giao dich (service @Transactional) vi tap loai su co duoc nap lazy.
        return jpa.findById(thoId).map(HoSoThoRepositoryAdapter::sangDomain);
    }

    @Override
    public void datTrangThaiOnline(Integer thoId, boolean online) {
        jpa.datOnline(thoId, online);
    }

    @Override
    public boolean datBanNeuRanh(Integer thoId) {
        return jpa.datBanNeuRanh(thoId) == 1;
    }

    @Override
    public void datRanh(Integer thoId) {
        jpa.datRanh(thoId);
    }

    @Override
    public void datDiemTrungBinh(Integer thoId, double diem) {
        jpa.datDiemTrungBinh(thoId, BigDecimal.valueOf(diem).setScale(2, RoundingMode.HALF_UP));
    }

    @Override
    public List<ThoTomTat> lietKe(boolean chiChuaDuyet) {
        List<ThoTomTatRow> rows = chiChuaDuyet ? jpa.lietKeChuaDuyet() : jpa.lietKeTatCa();
        return rows.stream()
                .map(r -> new ThoTomTat(r.id(), r.hoTen(), r.soDienThoai(), r.daDuyet(), r.online(), r.dangBan(),
                        r.diemTrungBinh().doubleValue()))
                .toList();
    }

    @Override
    public void duyetTho(Integer thoId, Set<Integer> loaiSuCoIds) {
        ThoEntity tho = jpa.findById(thoId)
                .orElseThrow(() -> new IllegalStateException("Khong tim thay tho " + thoId));
        tho.setDaDuyet(true);
        tho.setLoaiSuCoLamDuoc(new HashSet<>(loaiSuCoJpa.findAllById(loaiSuCoIds)));
        jpa.save(tho);
    }

    @Override
    public List<UngVienTho> timUngVienRanh(ViTri tam, double banKinhKm, Integer loaiSuCoId, LocalDateTime viTriMoiTu) {
        double dLat = banKinhKm / KM_MOI_DO;
        double dLng = banKinhKm / (KM_MOI_DO * Math.max(0.01, Math.cos(Math.toRadians(tam.latitude()))));
        List<UngVienRow> rows = jpa.timUngVien(
                ToaDo.sangDecimal(Math.max(-90.0, tam.latitude() - dLat)),
                ToaDo.sangDecimal(Math.min(90.0, tam.latitude() + dLat)),
                ToaDo.sangDecimal(Math.max(-180.0, tam.longitude() - dLng)),
                ToaDo.sangDecimal(Math.min(180.0, tam.longitude() + dLng)),
                viTriMoiTu, loaiSuCoId);
        return rows.stream()
                .map(r -> new UngVienTho(r.thoId(), ToaDo.sangViTri(r.latitude(), r.longitude()),
                        r.diemTrungBinh().doubleValue(), r.tiLeNhanCuoc().doubleValue()))
                .toList();
    }

    private static HoSoTho sangDomain(ThoEntity e) {
        BigDecimal diem = e.getDiemTrungBinh();
        BigDecimal tiLe = e.getTiLeNhanCuoc();
        return new HoSoTho(e.getId(), e.isTrangThaiOnline(), e.isDangBan(), e.isDaDuyet(),
                diem.doubleValue(), tiLe.doubleValue(),
                e.getLoaiSuCoLamDuoc().stream().map(LoaiSuCoEntity::getId).collect(Collectors.toSet()));
    }
}
