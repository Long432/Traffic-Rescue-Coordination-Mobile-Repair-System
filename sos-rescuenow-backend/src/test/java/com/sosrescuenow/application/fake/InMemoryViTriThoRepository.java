package com.sosrescuenow.application.fake;

import com.sosrescuenow.application.port.ViTriThoRepository;
import com.sosrescuenow.domain.model.ViTri;
import com.sosrescuenow.domain.model.ViTriTho;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryViTriThoRepository implements ViTriThoRepository {

    private final Map<Integer, ViTriTho> duLieu = new ConcurrentHashMap<>();

    @Override
    public void luu(Integer thoId, ViTri viTri, LocalDateTime luc) {
        duLieu.put(thoId, new ViTriTho(thoId, viTri, luc));
    }

    @Override
    public Optional<ViTriTho> timTheoThoId(Integer thoId) {
        return Optional.ofNullable(duLieu.get(thoId));
    }
}
