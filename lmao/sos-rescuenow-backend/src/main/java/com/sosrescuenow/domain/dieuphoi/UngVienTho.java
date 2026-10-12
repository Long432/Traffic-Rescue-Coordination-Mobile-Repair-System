package com.sosrescuenow.domain.dieuphoi;

import com.sosrescuenow.domain.model.ViTri;

/** Tho dang ranh va o gan, du dieu kien de duoc xem xet cho mot cuoc. */
public record UngVienTho(Integer thoId, ViTri viTri, double diemTrungBinh, double tiLeNhanCuoc) {
}
