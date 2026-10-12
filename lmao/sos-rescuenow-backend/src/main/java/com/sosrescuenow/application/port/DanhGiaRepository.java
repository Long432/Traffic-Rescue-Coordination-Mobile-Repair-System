package com.sosrescuenow.application.port;

import com.sosrescuenow.domain.model.DanhGia;

import java.util.List;
import java.util.Optional;

public interface DanhGiaRepository {

    Optional<DanhGia> timTheoCuoc(Long cuocId);

    /** @throws com.sosrescuenow.domain.exception.XungDotException neu cuoc nay da duoc danh gia */
    void luu(DanhGia danhGia);

    /** Tat ca so sao khach da cham cho tho nay (qua cac cuoc tho do da lam). */
    List<Integer> soSaoCuaTho(Integer thoId);
}
