package com.sosrescuenow.domain.model;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Mot cuoc cuu ho. Bat bien: thay doi trang thai duoc thuc hien bang cau lenh UPDATE co dieu kien
 * o tang luu tru (de chong hai nguoi cung sua), roi doc lai doi tuong moi.
 * Moi moc thoi gian la gio UTC.
 */
public final class CuocCuuHo {

    private final Long id; // null khi chua luu
    private final Integer khachId;
    private final Integer thoId; // null khi chua co tho
    private final Integer loaiSuCoId;
    private final ViTri viTri;
    private final String diaChi;
    private final String ghiChu;
    private final TrangThaiCuoc trangThai;
    private final BigDecimal giaUocTinh;
    private final BigDecimal tongTien;
    private final String lyDoHuy;
    private final LocalDateTime ngayTao;
    private final LocalDateTime batDauTimLuc;

    private CuocCuuHo(Long id, Integer khachId, Integer thoId, Integer loaiSuCoId, ViTri viTri,
                      String diaChi, String ghiChu, TrangThaiCuoc trangThai, BigDecimal giaUocTinh,
                      BigDecimal tongTien, String lyDoHuy, LocalDateTime ngayTao, LocalDateTime batDauTimLuc) {
        this.id = id;
        this.khachId = Objects.requireNonNull(khachId, "khachId");
        this.thoId = thoId;
        this.loaiSuCoId = Objects.requireNonNull(loaiSuCoId, "loaiSuCoId");
        this.viTri = Objects.requireNonNull(viTri, "viTri");
        this.diaChi = diaChi;
        this.ghiChu = ghiChu;
        this.trangThai = Objects.requireNonNull(trangThai, "trangThai");
        this.giaUocTinh = Objects.requireNonNull(giaUocTinh, "giaUocTinh");
        this.tongTien = tongTien;
        this.lyDoHuy = lyDoHuy;
        this.ngayTao = Objects.requireNonNull(ngayTao, "ngayTao");
        this.batDauTimLuc = Objects.requireNonNull(batDauTimLuc, "batDauTimLuc");
    }

    /** Cuoc moi: dang tim tho, bat dau dem thoi gian tim tu {@code bayGio}. */
    public static CuocCuuHo moi(Integer khachId, Integer loaiSuCoId, ViTri viTri, String diaChi,
                                String ghiChu, BigDecimal giaUocTinh, LocalDateTime bayGio) {
        return new CuocCuuHo(null, khachId, null, loaiSuCoId, viTri, diaChi, ghiChu,
                TrangThaiCuoc.DANG_TIM, giaUocTinh, null, null, bayGio, bayGio);
    }

    /** Dung lai doi tuong tu du lieu da luu (chi tang ha tang va test goi). */
    public static CuocCuuHo taiTao(Long id, Integer khachId, Integer thoId, Integer loaiSuCoId, ViTri viTri,
                                   String diaChi, String ghiChu, TrangThaiCuoc trangThai, BigDecimal giaUocTinh,
                                   BigDecimal tongTien, String lyDoHuy, LocalDateTime ngayTao,
                                   LocalDateTime batDauTimLuc) {
        return new CuocCuuHo(Objects.requireNonNull(id, "id"), khachId, thoId, loaiSuCoId, viTri, diaChi, ghiChu,
                trangThai, giaUocTinh, tongTien, lyDoHuy, ngayTao, batDauTimLuc);
    }

    /** Da tim tho duoc bao lau (khong am). Dung de chon giai doan phat cuoc. */
    public Duration tuoiTimKiem(LocalDateTime bayGio) {
        Duration tuoi = Duration.between(batDauTimLuc, bayGio);
        return tuoi.isNegative() ? Duration.ZERO : tuoi;
    }

    public boolean dangHoatDong() {
        return !trangThai.laKetThuc();
    }

    public Long getId() { return id; }
    public Integer getKhachId() { return khachId; }
    public Integer getThoId() { return thoId; }
    public Integer getLoaiSuCoId() { return loaiSuCoId; }
    public ViTri getViTri() { return viTri; }
    public String getDiaChi() { return diaChi; }
    public String getGhiChu() { return ghiChu; }
    public TrangThaiCuoc getTrangThai() { return trangThai; }
    public BigDecimal getGiaUocTinh() { return giaUocTinh; }
    public BigDecimal getTongTien() { return tongTien; }
    public String getLyDoHuy() { return lyDoHuy; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public LocalDateTime getBatDauTimLuc() { return batDauTimLuc; }
}
