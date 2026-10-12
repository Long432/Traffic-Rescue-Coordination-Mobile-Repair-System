package com.sosrescuenow.application.fake;

import com.sosrescuenow.application.port.LoaiSuCoRepository;
import com.sosrescuenow.domain.model.LoaiSuCo;

import java.util.List;
import java.util.Optional;

public class InMemoryLoaiSuCoRepository implements LoaiSuCoRepository {

    private final List<LoaiSuCo> duLieu;

    public InMemoryLoaiSuCoRepository(LoaiSuCo... loai) {
        this.duLieu = List.of(loai);
    }

    @Override
    public Optional<LoaiSuCo> timTheoId(Integer id) {
        return duLieu.stream().filter(l -> l.id().equals(id)).findFirst();
    }

    @Override
    public List<LoaiSuCo> timTatCa() {
        return duLieu;
    }
}
