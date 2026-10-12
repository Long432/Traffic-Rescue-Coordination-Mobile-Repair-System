package com.sosrescuenow.application.port;

import com.sosrescuenow.domain.model.TrangThaiCuoc;

public interface LichSuTrangThaiRepository {

    /** Ghi lai mot lan doi trang thai. {@code tu} la null khi cuoc vua duoc tao. */
    void ghi(Long cuocId, TrangThaiCuoc tu, TrangThaiCuoc den, Integer nguoiThucHienId, String ghiChu);
}
