package com.sosrescuenow.domain.model;

import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Nguoi dung (khach, tho hoac admin). Bat bien (immutable): moi thay doi tao ra doi tuong moi.
 * Moi moc thoi gian trong he thong deu la gio UTC.
 */
public final class NguoiDung {

    private final Integer id; // null khi chua luu vao CSDL
    private final String hoTen;
    private final String soDienThoai;
    private final String matKhauHash;
    private final VaiTro vaiTro;
    private final TrangThaiTaiKhoan trangThaiTaiKhoan;
    private final LocalDateTime khoaDen;

    private NguoiDung(Integer id, String hoTen, String soDienThoai, String matKhauHash,
                      VaiTro vaiTro, TrangThaiTaiKhoan trangThaiTaiKhoan, LocalDateTime khoaDen) {
        this.id = id;
        this.hoTen = batBuoc(hoTen, "Họ tên");
        this.soDienThoai = batBuoc(soDienThoai, "Số điện thoại");
        this.matKhauHash = batBuoc(matKhauHash, "Mật khẩu");
        this.vaiTro = Objects.requireNonNull(vaiTro, "vaiTro");
        this.trangThaiTaiKhoan = Objects.requireNonNull(trangThaiTaiKhoan, "trangThaiTaiKhoan");
        this.khoaDen = khoaDen;
    }

    /** Tao nguoi dung moi (chua co id), mac dinh dang hoat dong. */
    public static NguoiDung moi(String hoTen, String soDienThoai, String matKhauHash, VaiTro vaiTro) {
        return new NguoiDung(null, hoTen, soDienThoai, matKhauHash, vaiTro, TrangThaiTaiKhoan.HOAT_DONG, null);
    }

    /** Dung lai doi tuong tu du lieu da luu (chi tang ha tang goi). */
    public static NguoiDung taiTao(Integer id, String hoTen, String soDienThoai, String matKhauHash,
                                   VaiTro vaiTro, TrangThaiTaiKhoan trangThaiTaiKhoan, LocalDateTime khoaDen) {
        return new NguoiDung(Objects.requireNonNull(id, "id"), hoTen, soDienThoai, matKhauHash,
                vaiTro, trangThaiTaiKhoan, khoaDen);
    }

    /**
     * Quy tac khoa tai khoan:
     * - Co khoaDen: bi khoa cho den het thoi diem do (khoa tam, vi du huy qua 3 cuoc/ngay).
     * - Khong co khoaDen: bi khoa neu trang thai la BI_KHOA (admin khoa vo thoi han).
     */
    public boolean dangBiKhoa(LocalDateTime bayGio) {
        if (khoaDen != null) {
            return khoaDen.isAfter(bayGio);
        }
        return trangThaiTaiKhoan == TrangThaiTaiKhoan.BI_KHOA;
    }

    private static String batBuoc(String giaTri, String ten) {
        if (giaTri == null || giaTri.isBlank()) {
            throw new DuLieuKhongHopLeException(ten + " không được để trống");
        }
        return giaTri;
    }

    public Integer getId() { return id; }
    public String getHoTen() { return hoTen; }
    public String getSoDienThoai() { return soDienThoai; }
    public String getMatKhauHash() { return matKhauHash; }
    public VaiTro getVaiTro() { return vaiTro; }
    public TrangThaiTaiKhoan getTrangThaiTaiKhoan() { return trangThaiTaiKhoan; }
    public LocalDateTime getKhoaDen() { return khoaDen; }
}
