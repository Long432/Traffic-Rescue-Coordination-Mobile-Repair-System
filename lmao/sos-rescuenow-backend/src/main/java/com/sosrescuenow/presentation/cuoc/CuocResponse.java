package com.sosrescuenow.presentation.cuoc;

import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.TrangThaiCuoc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;

/** Thoi gian tra ve dang ISO-8601 co hau to Z (UTC) de app mobile doi sang gio dia phuong. */
public record CuocResponse(Long id, Integer khachId, Integer thoId, Integer loaiSuCoId,
                           double latitude, double longitude, String diaChi, String ghiChu,
                           TrangThaiCuoc trangThai, BigDecimal giaUocTinh, BigDecimal tongTien,
                           String lyDoHuy, Instant ngayTao) {

    public static CuocResponse tu(CuocCuuHo c) {
        return new CuocResponse(c.getId(), c.getKhachId(), c.getThoId(), c.getLoaiSuCoId(),
                c.getViTri().latitude(), c.getViTri().longitude(), c.getDiaChi(), c.getGhiChu(),
                c.getTrangThai(), c.getGiaUocTinh(), c.getTongTien(), c.getLyDoHuy(),
                c.getNgayTao().toInstant(ZoneOffset.UTC));
    }
}
