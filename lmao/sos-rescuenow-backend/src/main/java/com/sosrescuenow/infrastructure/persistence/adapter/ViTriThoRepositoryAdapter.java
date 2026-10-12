package com.sosrescuenow.infrastructure.persistence.adapter;

import com.sosrescuenow.application.port.ViTriThoRepository;
import com.sosrescuenow.domain.model.ViTri;
import com.sosrescuenow.domain.model.ViTriTho;
import com.sosrescuenow.infrastructure.persistence.entity.ViTriThoEntity;
import com.sosrescuenow.infrastructure.persistence.repository.ViTriThoJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
class ViTriThoRepositoryAdapter implements ViTriThoRepository {

    private final ViTriThoJpaRepository jpa;

    ViTriThoRepositoryAdapter(ViTriThoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void luu(Integer thoId, ViTri viTri, LocalDateTime luc) {
        ViTriThoEntity e = new ViTriThoEntity();
        e.setThoId(thoId);
        e.setLatitude(ToaDo.sangDecimal(viTri.latitude()));
        e.setLongitude(ToaDo.sangDecimal(viTri.longitude()));
        e.setCapNhatLuc(luc);
        jpa.save(e); // khoa chinh la ThoId: save() ghi de dong cu hoac them dong moi
    }

    @Override
    public Optional<ViTriTho> timTheoThoId(Integer thoId) {
        return jpa.findById(thoId).map(e -> new ViTriTho(
                e.getThoId(), ToaDo.sangViTri(e.getLatitude(), e.getLongitude()), e.getCapNhatLuc()));
    }
}
