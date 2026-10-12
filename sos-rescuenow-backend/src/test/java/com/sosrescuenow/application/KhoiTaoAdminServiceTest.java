package com.sosrescuenow.application;

import com.sosrescuenow.application.admin.KhoiTaoAdminService;
import com.sosrescuenow.application.fake.InMemoryNguoiDungRepository;
import com.sosrescuenow.application.port.PasswordHasher;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.model.NguoiDung;
import com.sosrescuenow.domain.model.VaiTro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KhoiTaoAdminServiceTest {

    private InMemoryNguoiDungRepository repo;
    private KhoiTaoAdminService service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryNguoiDungRepository();
        PasswordHasher hasher = new PasswordHasher() {
            @Override public String bam(String s) { return "H:" + s; }
            @Override public boolean khop(String s, String h) { return h.equals("H:" + s); }
        };
        service = new KhoiTaoAdminService(repo, hasher);
    }

    @Test
    void taoAdminMoi_vaiTroAdminVaMatKhauDaBam() {
        assertThat(service.taoNeuChuaCo("0900000099", "admin12345")).isTrue();

        NguoiDung nd = repo.timTheoSoDienThoai("0900000099").orElseThrow();
        assertThat(nd.getVaiTro()).isEqualTo(VaiTro.ADMIN);
        assertThat(nd.getMatKhauHash()).isEqualTo("H:admin12345");
    }

    @Test
    void goiLai_khongTaoThemVaKhongGhiDeMatKhau() {
        service.taoNeuChuaCo("0900000099", "admin12345");

        assertThat(service.taoNeuChuaCo("0900000099", "mat-khau-khac-123")).isFalse();
        assertThat(repo.timTheoSoDienThoai("0900000099").orElseThrow().getMatKhauHash()).isEqualTo("H:admin12345");
    }

    @Test
    void soDienThoaiDaLaTaiKhoanKhach_khongBiNangQuyen() {
        repo.luu(NguoiDung.moi("Khach", "0912345678", "H:x", VaiTro.USER));

        assertThat(service.taoNeuChuaCo("0912345678", "admin12345")).isFalse();
        assertThat(repo.timTheoSoDienThoai("0912345678").orElseThrow().getVaiTro()).isEqualTo(VaiTro.USER);
    }

    @Test
    void duLieuKhongHopLe_biTuChoi() {
        assertThatThrownBy(() -> service.taoNeuChuaCo("12345", "admin12345")).isInstanceOf(DuLieuKhongHopLeException.class);
        assertThatThrownBy(() -> service.taoNeuChuaCo(null, "admin12345")).isInstanceOf(DuLieuKhongHopLeException.class);
        assertThatThrownBy(() -> service.taoNeuChuaCo("0900000099", "ngan")).isInstanceOf(DuLieuKhongHopLeException.class);
        assertThatThrownBy(() -> service.taoNeuChuaCo("0900000099", null)).isInstanceOf(DuLieuKhongHopLeException.class);
        assertThat(repo.timTheoSoDienThoai("0900000099").isEmpty()).isTrue();
    }
}
