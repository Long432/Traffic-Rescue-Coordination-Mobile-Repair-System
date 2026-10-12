package com.sosrescuenow.infrastructure.persistence.adapter;

import com.sosrescuenow.application.port.LichSuTrangThaiRepository;
import com.sosrescuenow.domain.model.TrangThaiCuoc;
import com.sosrescuenow.infrastructure.persistence.entity.LichSuTrangThaiEntity;
import com.sosrescuenow.infrastructure.persistence.repository.LichSuTrangThaiJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
class LichSuTrangThaiRepositoryAdapter implements LichSuTrangThaiRepository {

    private static final int DO_DAI_GHI_CHU_TOI_DA = 255;

    private final LichSuTrangThaiJpaRepository jpa;

    LichSuTrangThaiRepositoryAdapter(LichSuTrangThaiJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void ghi(Long cuocId, TrangThaiCuoc tu, TrangThaiCuoc den, Integer nguoiThucHienId, String ghiChu) {
        LichSuTrangThaiEntity e = new LichSuTrangThaiEntity();
        e.setCuocId(cuocId);
        e.setTuTrangThai(tu);
        e.setDenTrangThai(den);
        e.setNguoiThucHienId(nguoiThucHienId);
        e.setGhiChu(ghiChu != null && ghiChu.length() > DO_DAI_GHI_CHU_TOI_DA
                ? ghiChu.substring(0, DO_DAI_GHI_CHU_TOI_DA) : ghiChu);
        jpa.save(e);
    }
}
