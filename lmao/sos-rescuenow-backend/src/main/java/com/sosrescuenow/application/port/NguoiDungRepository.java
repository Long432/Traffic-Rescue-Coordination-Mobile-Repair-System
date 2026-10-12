package com.sosrescuenow.application.port;

import com.sosrescuenow.domain.model.NguoiDung;

import java.util.Optional;

/** Cong ra (port): luu tru nguoi dung. Tang ha tang cai dat. */
public interface NguoiDungRepository {

    Optional<NguoiDung> timTheoId(Integer id);

    Optional<NguoiDung> timTheoSoDienThoai(String soDienThoai);

    boolean tonTaiSoDienThoai(String soDienThoai);

    /** @throws com.sosrescuenow.domain.exception.TrungLapException neu so dien thoai da ton tai */
    NguoiDung luu(NguoiDung nguoiDung);
}
