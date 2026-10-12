package com.sosrescuenow.domain;

import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.model.NguoiDung;
import com.sosrescuenow.domain.model.TrangThaiTaiKhoan;
import com.sosrescuenow.domain.model.VaiTro;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NguoiDungTest {

    private static final LocalDateTime BAY_GIO = LocalDateTime.of(2026, 10, 5, 8, 0);

    private static NguoiDung taiKhoan(TrangThaiTaiKhoan trangThai, LocalDateTime khoaDen) {
        return NguoiDung.taiTao(1, "An", "0912345678", "HASH", VaiTro.USER, trangThai, khoaDen);
    }

    @Test
    void taiKhoanMoiDangHoatDongVaKhongBiKhoa() {
        NguoiDung moi = NguoiDung.moi("An", "0912345678", "HASH", VaiTro.USER);

        assertThat(moi.getId()).isNull();
        assertThat(moi.getTrangThaiTaiKhoan()).isEqualTo(TrangThaiTaiKhoan.HOAT_DONG);
        assertThat(moi.dangBiKhoa(BAY_GIO)).isFalse();
    }

    @Test
    void khoaTam_conHieuLucKhiChuaDenKhoaDen() {
        assertThat(taiKhoan(TrangThaiTaiKhoan.BI_KHOA, BAY_GIO.plusHours(1)).dangBiKhoa(BAY_GIO)).isTrue();
    }

    @Test
    void khoaTam_hetHieuLucKhiDaQuaKhoaDen() {
        assertThat(taiKhoan(TrangThaiTaiKhoan.BI_KHOA, BAY_GIO.minusMinutes(1)).dangBiKhoa(BAY_GIO)).isFalse();
    }

    @Test
    void khoaVoThoiHan_khiBiKhoaMaKhongCoKhoaDen() {
        assertThat(taiKhoan(TrangThaiTaiKhoan.BI_KHOA, null).dangBiKhoa(BAY_GIO)).isTrue();
    }

    @Test
    void tuChoiHoTenRong() {
        assertThatThrownBy(() -> NguoiDung.moi("  ", "0912345678", "HASH", VaiTro.USER))
                .isInstanceOf(DuLieuKhongHopLeException.class);
    }
}
