package com.sosrescuenow.application.fake;

import com.sosrescuenow.application.port.DanhGiaRepository;
import com.sosrescuenow.domain.exception.XungDotException;
import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.DanhGia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDanhGiaRepository implements DanhGiaRepository {

    private final Map<Long, DanhGia> duLieu = new ConcurrentHashMap<>();
    private final InMemoryCuocCuuHoRepository cuocRepo;

    public InMemoryDanhGiaRepository(InMemoryCuocCuuHoRepository cuocRepo) {
        this.cuocRepo = cuocRepo;
    }

    @Override
    public Optional<DanhGia> timTheoCuoc(Long cuocId) {
        return Optional.ofNullable(duLieu.get(cuocId));
    }

    @Override
    public void luu(DanhGia danhGia) {
        if (duLieu.putIfAbsent(danhGia.cuocId(), danhGia) != null) {
            throw new XungDotException("Bạn đã đánh giá cuốc này rồi");
        }
    }

    @Override
    public List<Integer> soSaoCuaTho(Integer thoId) {
        List<Integer> kq = new ArrayList<>();
        for (DanhGia d : duLieu.values()) {
            Optional<CuocCuuHo> cuoc = cuocRepo.timTheoId(d.cuocId());
            if (cuoc.isPresent() && thoId.equals(cuoc.get().getThoId())) {
                kq.add(d.soSao());
            }
        }
        return kq;
    }
}
