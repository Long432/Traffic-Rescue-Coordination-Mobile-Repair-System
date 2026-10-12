package com.sosrescuenow.domain.model;

import java.util.Set;

/** Ho so tho: trang thai san sang va cac loai su co tho lam duoc. */
public record HoSoTho(Integer id, boolean online, boolean dangBan, boolean daDuyet,
                      double diemTrungBinh, double tiLeNhanCuoc, Set<Integer> loaiSuCoLamDuoc) {

    public HoSoTho {
        loaiSuCoLamDuoc = Set.copyOf(loaiSuCoLamDuoc);
    }

    public boolean lamDuoc(Integer loaiSuCoId) {
        return loaiSuCoLamDuoc.contains(loaiSuCoId);
    }

    /** San sang nhan viec: da duoc duyet, dang online va khong ban. */
    public boolean sanSang() {
        return daDuyet && online && !dangBan;
    }
}
