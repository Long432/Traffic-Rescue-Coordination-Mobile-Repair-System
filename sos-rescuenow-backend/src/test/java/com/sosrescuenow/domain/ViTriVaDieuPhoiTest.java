package com.sosrescuenow.domain;

import com.sosrescuenow.domain.dieuphoi.BangGia;
import com.sosrescuenow.domain.dieuphoi.ChamDiemGanNhat;
import com.sosrescuenow.domain.dieuphoi.ChamDiemTongHop;
import com.sosrescuenow.domain.dieuphoi.QuyTacPhatCuoc;
import com.sosrescuenow.domain.dieuphoi.UngVienTho;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.model.ViTri;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class ViTriVaDieuPhoiTest {

    // ---------- ViTri / Haversine ----------

    @Test
    void khoangCachCungDiemLaKhong() {
        ViTri a = new ViTri(10.7769, 106.7009);
        assertThat(a.khoangCachKm(a)).isZero();
    }

    @Test
    void motDoVidoXapXi111Km() {
        assertThat(new ViTri(10, 106).khoangCachKm(new ViTri(11, 106))).isCloseTo(111.2, within(0.3));
    }

    @Test
    void khoangCachDoiXung() {
        ViTri a = new ViTri(10.7769, 106.7009);
        ViTri b = new ViTri(10.8231, 106.6297);
        assertThat(a.khoangCachKm(b)).isEqualTo(b.khoangCachKm(a));
    }

    @Test
    void toaDoNgoaiPhamViBiTuChoi() {
        assertThatThrownBy(() -> new ViTri(91, 0)).isInstanceOf(DuLieuKhongHopLeException.class);
        assertThatThrownBy(() -> new ViTri(0, 181)).isInstanceOf(DuLieuKhongHopLeException.class);
        assertThatThrownBy(() -> new ViTri(Double.NaN, 0)).isInstanceOf(DuLieuKhongHopLeException.class);
    }

    // ---------- Giai doan phat cuoc ----------

    @Test
    void giaiDoanTheoThoiGianTim() {
        QuyTacPhatCuoc.GiaiDoan dau = QuyTacPhatCuoc.tai(Duration.ofSeconds(5));
        assertThat(dau.banKinhKm()).isEqualTo(5.0);
        assertThat(dau.soThoUuTien()).isEqualTo(3);
        assertThat(dau.hetHan()).isFalse();

        QuyTacPhatCuoc.GiaiDoan giua = QuyTacPhatCuoc.tai(Duration.ofSeconds(20));
        assertThat(giua.banKinhKm()).isEqualTo(5.0);
        assertThat(giua.soThoUuTien()).isNull();

        QuyTacPhatCuoc.GiaiDoan rong = QuyTacPhatCuoc.tai(Duration.ofSeconds(60));
        assertThat(rong.banKinhKm()).isEqualTo(10.0);
        assertThat(rong.hetHan()).isFalse();

        assertThat(QuyTacPhatCuoc.tai(Duration.ofSeconds(120)).hetHan()).isTrue();
        assertThat(QuyTacPhatCuoc.tai(Duration.ofSeconds(119)).hetHan()).isFalse();
    }

    // ---------- Cham diem ----------

    @Test
    void chamDiemGanNhat_ganHonDiemCaoHon() {
        UngVienTho u = new UngVienTho(1, new ViTri(0, 0), 1.0, 0.1);
        ChamDiemGanNhat cd = new ChamDiemGanNhat();
        assertThat(cd.cham(u, 1.0, 5.0)).isGreaterThan(cd.cham(u, 4.0, 5.0));
        assertThat(cd.cham(u, 0.0, 5.0)).isEqualTo(1.0);
        assertThat(cd.cham(u, 5.0, 5.0)).isZero();
    }

    @Test
    void chamDiemTongHop_congThucVaTrongSo() {
        UngVienTho u = new UngVienTho(1, new ViTri(0, 0), 4.0, 0.5);
        // 0.5 * (1 - 2.5/5) + 0.3 * (4/5) + 0.2 * 0.5 = 0.25 + 0.24 + 0.10 = 0.59
        assertThat(new ChamDiemTongHop().cham(u, 2.5, 5.0)).isCloseTo(0.59, within(1e-9));
    }

    @Test
    void chamDiemTongHop_danhGiaCaoCoTheThangThoGanHon() {
        ChamDiemTongHop cd = new ChamDiemTongHop();
        UngVienTho gan = new UngVienTho(1, new ViTri(0, 0), 2.0, 0.3);
        UngVienTho xaNhungTot = new UngVienTho(2, new ViTri(0, 0), 5.0, 1.0);
        assertThat(cd.cham(xaNhungTot, 1.5, 5.0)).isGreaterThan(cd.cham(gan, 1.0, 5.0));
    }

    @Test
    void chamDiemKhongVuotNgoaiKhoang0Den1() {
        UngVienTho u = new UngVienTho(1, new ViTri(0, 0), 99.0, 5.0); // du lieu bat thuong
        assertThat(new ChamDiemTongHop().cham(u, -3.0, 5.0)).isBetween(0.0, 1.0);
    }

    // ---------- Bang gia ----------

    @Test
    void banNgayKhongPhuPhi() {
        // 03:00 UTC = 10:00 gio Viet Nam
        assertThat(BangGia.tinhGia(new BigDecimal("80000"), LocalDateTime.of(2026, 10, 5, 3, 0)))
                .isEqualByComparingTo("80000");
    }

    @Test
    void banDemTang20PhanTram() {
        // 16:00 UTC = 23:00 gio Viet Nam
        assertThat(BangGia.tinhGia(new BigDecimal("80000"), LocalDateTime.of(2026, 10, 5, 16, 0)))
                .isEqualByComparingTo("96000");
    }

    @Test
    void ranhGioBanDem() {
        // 15:00 UTC = 22:00 VN (bat dau ban dem); 21:59 UTC = 04:59 VN (van ban dem); 22:00 UTC = 05:00 VN (het)
        assertThat(BangGia.tinhGia(new BigDecimal("100000"), LocalDateTime.of(2026, 10, 5, 15, 0)))
                .isEqualByComparingTo("120000");
        assertThat(BangGia.tinhGia(new BigDecimal("100000"), LocalDateTime.of(2026, 10, 5, 21, 59)))
                .isEqualByComparingTo("120000");
        assertThat(BangGia.tinhGia(new BigDecimal("100000"), LocalDateTime.of(2026, 10, 5, 22, 0)))
                .isEqualByComparingTo("100000");
    }

    @Test
    void lamTronDenNghinDong() {
        // 60500 * 1.2 = 72600 -> 73000
        assertThat(BangGia.tinhGia(new BigDecimal("60500"), LocalDateTime.of(2026, 10, 5, 16, 0)))
                .isEqualByComparingTo("73000");
    }
}
