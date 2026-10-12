package com.sosrescuenow.infrastructure.persistence.adapter;

import com.sosrescuenow.application.port.LoaiSuCoRepository;
import com.sosrescuenow.domain.model.LoaiSuCo;
import com.sosrescuenow.infrastructure.persistence.entity.LoaiSuCoEntity;
import com.sosrescuenow.infrastructure.persistence.repository.LoaiSuCoJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class LoaiSuCoRepositoryAdapter implements LoaiSuCoRepository {

    private final LoaiSuCoJpaRepository jpa;

    LoaiSuCoRepositoryAdapter(LoaiSuCoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<LoaiSuCo> timTheoId(Integer id) {
        return jpa.findById(id).map(LoaiSuCoRepositoryAdapter::sangDomain);
    }

    @Override
    public List<LoaiSuCo> timTatCa() {
        return jpa.findAll(Sort.by("id")).stream().map(LoaiSuCoRepositoryAdapter::sangDomain).toList();
    }

    private static LoaiSuCo sangDomain(LoaiSuCoEntity e) {
        return new LoaiSuCo(e.getId(), e.getTenSuCo(), e.getGiaCoBan(), e.getMucKhanCap());
    }
}
