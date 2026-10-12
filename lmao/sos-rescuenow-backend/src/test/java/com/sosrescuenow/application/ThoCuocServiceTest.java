package com.sosrescuenow.application;

import com.sosrescuenow.application.cuoc.CuocQuanhToi;
import com.sosrescuenow.application.fake.MoiTruongTest;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.exception.KhongCoQuyenException;
import com.sosrescuenow.domain.exception.TrangThaiKhongHopLeException;
import com.sosrescuenow.domain.exception.XungDotException;
import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.HoSoTho;
import com.sosrescuenow.domain.model.TrangThaiCuoc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.sosrescuenow.application.fake.MoiTruongTest.DIEM_KHACH;
import static com.sosrescuenow.application.fake.MoiTruongTest.KHACH_AN;
import static com.sosrescuenow.application.fake.MoiTruongTest.cachKhachKm;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThoCuocServiceTest {

    private MoiTruongTest mt;

    @BeforeEach
    void setUp() {
        mt = new MoiTruongTest();
    }

    private HoSoTho hoSo(int thoId) {
        return mt.hoSoRepo.timHoSo(thoId).orElseThrow();
    }

    // ---------- Nhan cuoc ----------

    @Test
    void nhanCuoc_thanhCong_ganThoVaDanhDauBan() {
        mt.themThoSanSang(1, cachKhachKm(0.5), 4.8, 0.9);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);

        CuocCuuHo ketQua = mt.thoService.nhanCuoc(1, cuoc.getId());

        assertThat(ketQua.getTrangThai()).isEqualTo(TrangThaiCuoc.DA_NHAN);
        assertThat(ketQua.getThoId()).isEqualTo(1);
        assertThat(hoSo(1).dangBan()).isTrue();
        assertThat(mt.lichSu.tatCa()).hasSize(1);
        assertThat(mt.lichSu.tatCa().get(0).den()).isEqualTo(TrangThaiCuoc.DA_NHAN);
    }

    @Test
    void nhanCuoc_thoChuaDuocDuyet_biTuChoi() {
        mt.hoSoRepo.them(1, true, false, false, 5, 1, MoiTruongTest.LOAI_THUNG_LOP);
        mt.viTriRepo.luu(1, cachKhachKm(0.5), MoiTruongTest.BAY_GIO);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);

        assertThatThrownBy(() -> mt.thoService.nhanCuoc(1, cuoc.getId()))
                .isInstanceOf(KhongCoQuyenException.class);
        assertThat(mt.trangThaiCuoc(cuoc.getId())).isEqualTo(TrangThaiCuoc.DANG_TIM);
    }

    @Test
    void nhanCuoc_thoOffline_biTuChoi() {
        mt.hoSoRepo.them(1, false, false, true, 5, 1, MoiTruongTest.LOAI_THUNG_LOP);
        mt.viTriRepo.luu(1, cachKhachKm(0.5), MoiTruongTest.BAY_GIO);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);

        assertThatThrownBy(() -> mt.thoService.nhanCuoc(1, cuoc.getId()))
                .isInstanceOf(TrangThaiKhongHopLeException.class);
    }

    @Test
    void nhanCuoc_thoDangBan_biTuChoi() {
        mt.hoSoRepo.them(1, true, true, true, 5, 1, MoiTruongTest.LOAI_THUNG_LOP);
        mt.viTriRepo.luu(1, cachKhachKm(0.5), MoiTruongTest.BAY_GIO);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);

        assertThatThrownBy(() -> mt.thoService.nhanCuoc(1, cuoc.getId()))
                .isInstanceOf(XungDotException.class);
    }

    @Test
    void nhanCuoc_thoKhongLamDuocLoaiSuCo_biTuChoi() {
        mt.hoSoRepo.them(1, true, false, true, 5, 1, 99); // chi lam loai 99
        mt.viTriRepo.luu(1, cachKhachKm(0.5), MoiTruongTest.BAY_GIO);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);

        assertThatThrownBy(() -> mt.thoService.nhanCuoc(1, cuoc.getId()))
                .isInstanceOf(KhongCoQuyenException.class);
    }

    @Test
    void nhanCuoc_viTriQuaCu_biTuChoi() {
        mt.hoSoRepo.them(1, true, false, true, 5, 1, MoiTruongTest.LOAI_THUNG_LOP);
        mt.viTriRepo.luu(1, cachKhachKm(0.5), MoiTruongTest.BAY_GIO.minusMinutes(10)); // mat tin hieu
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);

        assertThatThrownBy(() -> mt.thoService.nhanCuoc(1, cuoc.getId()))
                .isInstanceOf(DuLieuKhongHopLeException.class);
    }

    @Test
    void nhanCuoc_banKinhMoRongTheoThoiGian() {
        mt.themThoSanSang(1, cachKhachKm(7.0), 5, 1); // 7 km
        CuocCuuHo cuocMoi = mt.themCuocDangTim(KHACH_AN, 30);   // ban kinh 5 km
        assertThatThrownBy(() -> mt.thoService.nhanCuoc(1, cuocMoi.getId()))
                .isInstanceOf(KhongCoQuyenException.class);

        MoiTruongTest mt2 = new MoiTruongTest();
        mt2.themThoSanSang(1, cachKhachKm(7.0), 5, 1);
        CuocCuuHo cuocLau = mt2.themCuocDangTim(KHACH_AN, 70);  // da mo rong 10 km
        assertThat(mt2.thoService.nhanCuoc(1, cuocLau.getId()).getThoId()).isEqualTo(1);
    }

    @Test
    void nhanCuoc_cuocHetHan_biTuChoi() {
        mt.themThoSanSang(1, cachKhachKm(0.5), 5, 1);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 125);

        assertThatThrownBy(() -> mt.thoService.nhanCuoc(1, cuoc.getId()))
                .isInstanceOf(KhongCoQuyenException.class);
    }

    @Test
    void giaiDoanUuTien_chiTop3NhanDuocRoiMoChoTatCa() {
        // 4 tho cach khach 0.5, 1.0, 1.5, 2.0 km, cung danh gia -> tho 4 xa nhat bi xep sau top 3
        for (int i = 1; i <= 4; i++) {
            mt.themThoSanSang(i, cachKhachKm(0.5 * i), 5, 1);
        }
        CuocCuuHo cuocMoi = mt.themCuocDangTim(KHACH_AN, 5);   // 5s: chi top 3
        assertThatThrownBy(() -> mt.thoService.nhanCuoc(4, cuocMoi.getId()))
                .isInstanceOf(KhongCoQuyenException.class);
        assertThat(mt.thoService.cuocQuanhToi(4)).isEmpty();
        assertThat(mt.thoService.cuocQuanhToi(1)).hasSize(1);

        MoiTruongTest mt2 = new MoiTruongTest();
        for (int i = 1; i <= 4; i++) {
            mt2.themThoSanSang(i, cachKhachKm(0.5 * i), 5, 1);
        }
        CuocCuuHo cuocSau = mt2.themCuocDangTim(KHACH_AN, 25); // 25s: mo cho tat ca
        assertThat(mt2.thoService.nhanCuoc(4, cuocSau.getId()).getThoId()).isEqualTo(4);
    }

    @Test
    void danhSachCuocQuanhToi_sapXepGanNhatTruoc() {
        mt.themThoSanSang(1, DIEM_KHACH, 5, 1);
        CuocCuuHo xa = mt.cuocRepo.them(CuocCuuHo.moi(KHACH_AN + 1, MoiTruongTest.LOAI_THUNG_LOP, cachKhachKm(3.0),
                null, null, new java.math.BigDecimal("80000"), MoiTruongTest.BAY_GIO.minusSeconds(30)));
        CuocCuuHo gan = mt.cuocRepo.them(CuocCuuHo.moi(KHACH_AN + 2, MoiTruongTest.LOAI_THUNG_LOP, cachKhachKm(1.0),
                null, null, new java.math.BigDecimal("80000"), MoiTruongTest.BAY_GIO.minusSeconds(30)));

        List<CuocQuanhToi> ds = mt.thoService.cuocQuanhToi(1);

        assertThat(ds).extracting(c -> c.cuoc().getId()).containsExactly(gan.getId(), xa.getId());
        assertThat(ds.get(0).khoangCachKm()).isLessThan(ds.get(1).khoangCachKm());
    }

    // ---------- Dong thoi ----------

    @Test
    void nhanCuoc_muoiThoCungBam_chiMotThoThanhCong() throws Exception {
        int soTho = 10;
        for (int i = 1; i <= soTho; i++) {
            mt.themThoSanSang(i, cachKhachKm(0.1 * i), 5, 1);
        }
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);

        ExecutorService pool = Executors.newFixedThreadPool(soTho);
        CountDownLatch batDau = new CountDownLatch(1);
        List<Future<Integer>> ketQua = new ArrayList<>();
        for (int i = 1; i <= soTho; i++) {
            final int thoId = i;
            Callable<Integer> viec = () -> {
                batDau.await();
                try {
                    mt.thoService.nhanCuoc(thoId, cuoc.getId());
                    return thoId;           // thanh cong
                } catch (XungDotException e) {
                    return -1;              // thua cuoc
                }
            };
            ketQua.add(pool.submit(viec));
        }
        batDau.countDown(); // tat ca cung xuat phat
        List<Integer> thanhCong = new ArrayList<>();
        for (Future<Integer> f : ketQua) {
            Integer v = f.get(10, TimeUnit.SECONDS);
            if (v > 0) {
                thanhCong.add(v);
            }
        }
        pool.shutdown();

        assertThat(thanhCong).hasSize(1);
        int thang = thanhCong.get(0);
        CuocCuuHo sau = mt.cuocRepo.timTheoId(cuoc.getId()).orElseThrow();
        assertThat(sau.getThoId()).isEqualTo(thang);
        assertThat(sau.getTrangThai()).isEqualTo(TrangThaiCuoc.DA_NHAN);
        for (int i = 1; i <= soTho; i++) {
            assertThat(hoSo(i).dangBan()).as("tho " + i).isEqualTo(i == thang);
        }
    }

    // ---------- Tien do ----------

    private CuocCuuHo thoNhanCuoc() {
        mt.themThoSanSang(1, cachKhachKm(0.5), 5, 1);
        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);
        mt.thoService.nhanCuoc(1, cuoc.getId());
        return cuoc;
    }

    @Test
    void luongDayDu_tuNhanDenHoanThanh() {
        CuocCuuHo cuoc = thoNhanCuoc();
        Long id = cuoc.getId();

        mt.thoService.chuyenTrangThai(1, id, TrangThaiCuoc.DANG_DEN);
        mt.thoService.capNhatViTri(1, DIEM_KHACH.latitude(), DIEM_KHACH.longitude()); // tho toi noi
        mt.thoService.chuyenTrangThai(1, id, TrangThaiCuoc.DA_DEN);
        mt.thoService.chuyenTrangThai(1, id, TrangThaiCuoc.DANG_SUA);
        CuocCuuHo choTt = mt.thoService.chuyenTrangThai(1, id, TrangThaiCuoc.CHO_THANH_TOAN);
        assertThat(choTt.getTongTien()).isEqualByComparingTo("80000");
        CuocCuuHo xong = mt.thoService.chuyenTrangThai(1, id, TrangThaiCuoc.HOAN_THANH);

        assertThat(xong.getTrangThai()).isEqualTo(TrangThaiCuoc.HOAN_THANH);
        assertThat(hoSo(1).dangBan()).isFalse();
        assertThat(mt.lichSu.tatCa()).extracting(d -> d.den()).containsExactly(
                TrangThaiCuoc.DA_NHAN, TrangThaiCuoc.DANG_DEN, TrangThaiCuoc.DA_DEN,
                TrangThaiCuoc.DANG_SUA, TrangThaiCuoc.CHO_THANH_TOAN, TrangThaiCuoc.HOAN_THANH);
    }

    @Test
    void daDen_khiConXaKhach_biTuChoi() {
        CuocCuuHo cuoc = thoNhanCuoc(); // tho dang cach khach 0.5 km
        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DANG_DEN);

        assertThatThrownBy(() -> mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DA_DEN))
                .isInstanceOf(DuLieuKhongHopLeException.class)
                .hasMessageContaining("chưa đến gần");
        assertThat(mt.trangThaiCuoc(cuoc.getId())).isEqualTo(TrangThaiCuoc.DANG_DEN);
    }

    @Test
    void chuyenNhayBuoc_biTuChoi() {
        CuocCuuHo cuoc = thoNhanCuoc();
        assertThatThrownBy(() -> mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DANG_SUA))
                .isInstanceOf(TrangThaiKhongHopLeException.class);
    }

    @Test
    void thoKhongDuocChuyenSangTrangThaiHuyHoacTimLai() {
        CuocCuuHo cuoc = thoNhanCuoc();
        assertThatThrownBy(() -> mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.HUY))
                .isInstanceOf(DuLieuKhongHopLeException.class);
        assertThatThrownBy(() -> mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DANG_TIM))
                .isInstanceOf(DuLieuKhongHopLeException.class);
    }

    @Test
    void thoKhacKhongDuocDungVaoCuoc() {
        CuocCuuHo cuoc = thoNhanCuoc();
        mt.themThoSanSang(2, cachKhachKm(0.3), 5, 1);

        assertThatThrownBy(() -> mt.thoService.chuyenTrangThai(2, cuoc.getId(), TrangThaiCuoc.DANG_DEN))
                .isInstanceOf(KhongCoQuyenException.class);
        assertThatThrownBy(() -> mt.thoService.thoTraCuoc(2, cuoc.getId(), "x"))
                .isInstanceOf(KhongCoQuyenException.class);
    }

    // ---------- Tra cuoc ----------

    @Test
    void thoTraCuoc_cuocQuayVeTimThoVaMocTimDuocDatLai() {
        CuocCuuHo cuoc = thoNhanCuoc();
        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DANG_DEN);

        CuocCuuHo sau = mt.thoService.thoTraCuoc(1, cuoc.getId(), "Hỏng xe");

        assertThat(sau.getTrangThai()).isEqualTo(TrangThaiCuoc.DANG_TIM);
        assertThat(sau.getThoId()).isNull();
        assertThat(sau.getBatDauTimLuc()).isEqualTo(MoiTruongTest.BAY_GIO); // dem lai tu bay gio
        assertThat(hoSo(1).dangBan()).isFalse();
        assertThat(mt.lichSu.tatCa().get(mt.lichSu.tatCa().size() - 1).ghiChu()).isEqualTo("Hỏng xe");
    }

    @Test
    void thoTraCuoc_saoKhiDaDenNoi_biTuChoi() {
        CuocCuuHo cuoc = thoNhanCuoc();
        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DANG_DEN);
        mt.thoService.capNhatViTri(1, DIEM_KHACH.latitude(), DIEM_KHACH.longitude());
        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DA_DEN);

        assertThatThrownBy(() -> mt.thoService.thoTraCuoc(1, cuoc.getId(), "Đổi ý"))
                .isInstanceOf(TrangThaiKhongHopLeException.class);
    }

    // ---------- Online / vi tri ----------

    @Test
    void batOnline_khiChuaDuyet_biTuChoi() {
        mt.hoSoRepo.taoHoSoMoi(7);
        assertThatThrownBy(() -> mt.thoService.datTrangThaiOnline(7, true))
                .isInstanceOf(KhongCoQuyenException.class);
    }

    @Test
    void tatOnline_khiDangCoCuoc_biTuChoi() {
        thoNhanCuoc();
        assertThatThrownBy(() -> mt.thoService.datTrangThaiOnline(1, false))
                .isInstanceOf(TrangThaiKhongHopLeException.class);
    }

    @Test
    void capNhatViTri_toaDoSai_biTuChoi() {
        mt.themThoSanSang(1, DIEM_KHACH, 5, 1);
        assertThatThrownBy(() -> mt.thoService.capNhatViTri(1, 123, 0))
                .isInstanceOf(DuLieuKhongHopLeException.class);
    }

    // ---------- Ho so va cuoc hien tai ----------

    @Test
    void hoSo_phanAnhTrangThaiThat() {
        mt.hoSoRepo.them(5, true, false, true, 4.5, 0.8, MoiTruongTest.LOAI_THUNG_LOP);

        HoSoTho h = mt.thoService.hoSo(5);

        assertThat(h.online()).isTrue();
        assertThat(h.daDuyet()).isTrue();
        assertThat(h.dangBan()).isFalse();
        assertThatThrownBy(() -> mt.thoService.hoSo(404)).isInstanceOf(
                com.sosrescuenow.domain.exception.KhongTimThayException.class);
    }

    @Test
    void cuocHienTai_rongKhiRanh_coKhiDangLam_rongSauKhiHoanThanh() {
        mt.themThoSanSang(1, cachKhachKm(0.5), 5, 1);
        assertThat(mt.thoService.cuocHienTai(1).isEmpty()).isTrue();

        CuocCuuHo cuoc = mt.themCuocDangTim(KHACH_AN, 30);
        mt.thoService.nhanCuoc(1, cuoc.getId());
        assertThat(mt.thoService.cuocHienTai(1).orElseThrow().getId()).isEqualTo(cuoc.getId());

        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DANG_DEN);
        mt.thoService.capNhatViTri(1, DIEM_KHACH.latitude(), DIEM_KHACH.longitude());
        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DA_DEN);
        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.DANG_SUA);
        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.CHO_THANH_TOAN);
        assertThat(mt.thoService.cuocHienTai(1).isPresent()).isTrue();   // chua thu tien thi van la cuoc dang lam

        mt.thoService.chuyenTrangThai(1, cuoc.getId(), TrangThaiCuoc.HOAN_THANH);
        assertThat(mt.thoService.cuocHienTai(1).isEmpty()).isTrue();
    }
}
