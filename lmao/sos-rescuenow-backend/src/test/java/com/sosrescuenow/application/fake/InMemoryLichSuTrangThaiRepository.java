package com.sosrescuenow.application.fake;

import com.sosrescuenow.application.port.LichSuTrangThaiRepository;
import com.sosrescuenow.domain.model.TrangThaiCuoc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryLichSuTrangThaiRepository implements LichSuTrangThaiRepository {

    public record Dong(Long cuocId, TrangThaiCuoc tu, TrangThaiCuoc den, Integer nguoiThucHienId, String ghiChu) {
    }

    private final List<Dong> cacDong = new CopyOnWriteArrayList<>();

    @Override
    public void ghi(Long cuocId, TrangThaiCuoc tu, TrangThaiCuoc den, Integer nguoiThucHienId, String ghiChu) {
        cacDong.add(new Dong(cuocId, tu, den, nguoiThucHienId, ghiChu));
    }

    public List<Dong> tatCa() {
        return List.copyOf(cacDong);
    }
}
