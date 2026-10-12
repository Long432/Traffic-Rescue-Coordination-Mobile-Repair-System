package com.sosrescuenow.application;

import com.sosrescuenow.application.auth.AuthResult;
import com.sosrescuenow.application.auth.AuthService;
import com.sosrescuenow.application.auth.DangKyCommand;
import com.sosrescuenow.application.auth.DangNhapCommand;
import com.sosrescuenow.application.port.HoSoThoRepository;
import com.sosrescuenow.application.port.NguoiDungRepository;
import com.sosrescuenow.application.port.PasswordHasher;
import com.sosrescuenow.application.port.TokenIssuer;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.exception.SaiThongTinDangNhapException;
import com.sosrescuenow.domain.exception.TaiKhoanBiKhoaException;
import com.sosrescuenow.domain.exception.TrungLapException;
import com.sosrescuenow.domain.model.NguoiDung;
import com.sosrescuenow.domain.model.TrangThaiTaiKhoan;
import com.sosrescuenow.domain.model.VaiTro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private static final String SDT = "0912345678";
    private static final LocalDateTime BAY_GIO = LocalDateTime.of(2026, 10, 5, 8, 0);

    private final Clock clock = Clock.fixed(BAY_GIO.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

    @Mock private NguoiDungRepository nguoiDungRepository;
    @Mock private HoSoThoRepository hoSoThoRepository;
    @Mock private PasswordHasher passwordHasher;
    @Mock private TokenIssuer tokenIssuer;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(nguoiDungRepository, hoSoThoRepository, passwordHasher, tokenIssuer, clock);
    }

    private static NguoiDung daLuu(int id, VaiTro vaiTro, TrangThaiTaiKhoan trangThai, LocalDateTime khoaDen) {
        return NguoiDung.taiTao(id, "Nguyễn An", SDT, "HASH", vaiTro, trangThai, khoaDen);
    }

    private void tokenPhatHanhDuoc(int id, VaiTro vaiTro) {
        when(tokenIssuer.phatHanh(id, vaiTro)).thenReturn("TOKEN");
        when(tokenIssuer.thoiHan()).thenReturn(Duration.ofHours(24));
    }

    // ---------- Dang ky ----------

    @Test
    void dangKyKhach_luuMatKhauDaBamVaKhongTaoHoSoTho() {
        when(nguoiDungRepository.tonTaiSoDienThoai(SDT)).thenReturn(false);
        when(passwordHasher.bam("matkhau123")).thenReturn("HASH");
        when(nguoiDungRepository.luu(any(NguoiDung.class)))
                .thenReturn(daLuu(1, VaiTro.USER, TrangThaiTaiKhoan.HOAT_DONG, null));
        tokenPhatHanhDuoc(1, VaiTro.USER);

        AuthResult kq = authService.dangKy(new DangKyCommand("  Nguyễn An ", SDT, "matkhau123", VaiTro.USER));

        ArgumentCaptor<NguoiDung> daGui = ArgumentCaptor.forClass(NguoiDung.class);
        verify(nguoiDungRepository).luu(daGui.capture());
        assertThat(daGui.getValue().getHoTen()).isEqualTo("Nguyễn An");
        assertThat(daGui.getValue().getMatKhauHash()).isEqualTo("HASH");
        assertThat(kq.accessToken()).isEqualTo("TOKEN");
        assertThat(kq.hetHanSauGiay()).isEqualTo(86_400);
        assertThat(kq.vaiTro()).isEqualTo(VaiTro.USER);
        verifyNoInteractions(hoSoThoRepository);
    }

    @Test
    void dangKyTho_taoHoSoTho() {
        when(nguoiDungRepository.tonTaiSoDienThoai(SDT)).thenReturn(false);
        when(passwordHasher.bam("matkhau123")).thenReturn("HASH");
        when(nguoiDungRepository.luu(any(NguoiDung.class)))
                .thenReturn(daLuu(7, VaiTro.THO, TrangThaiTaiKhoan.HOAT_DONG, null));
        tokenPhatHanhDuoc(7, VaiTro.THO);

        authService.dangKy(new DangKyCommand("Thợ Dũng", SDT, "matkhau123", VaiTro.THO));

        verify(hoSoThoRepository).taoHoSoMoi(7);
    }

    @Test
    void dangKyAdmin_biTuChoiVaKhongChamVaoCsdl() {
        assertThatThrownBy(() -> authService.dangKy(new DangKyCommand("Hacker", SDT, "matkhau123", VaiTro.ADMIN)))
                .isInstanceOf(DuLieuKhongHopLeException.class);

        verifyNoInteractions(nguoiDungRepository, hoSoThoRepository, passwordHasher, tokenIssuer);
    }

    @Test
    void dangKyTrungSoDienThoai_biTuChoi() {
        when(nguoiDungRepository.tonTaiSoDienThoai(SDT)).thenReturn(true);

        assertThatThrownBy(() -> authService.dangKy(new DangKyCommand("An", SDT, "matkhau123", VaiTro.USER)))
                .isInstanceOf(TrungLapException.class);

        verify(nguoiDungRepository, never()).luu(any());
        verifyNoInteractions(hoSoThoRepository, tokenIssuer);
    }

    // ---------- Dang nhap ----------

    @Test
    void dangNhapDung_traToken() {
        when(nguoiDungRepository.timTheoSoDienThoai(SDT))
                .thenReturn(Optional.of(daLuu(2, VaiTro.USER, TrangThaiTaiKhoan.HOAT_DONG, null)));
        when(passwordHasher.khop("matkhau123", "HASH")).thenReturn(true);
        tokenPhatHanhDuoc(2, VaiTro.USER);

        AuthResult kq = authService.dangNhap(new DangNhapCommand(SDT, "matkhau123"));

        assertThat(kq.accessToken()).isEqualTo("TOKEN");
        assertThat(kq.nguoiDungId()).isEqualTo(2);
    }

    @Test
    void dangNhapSaiMatKhau_biTuChoi() {
        when(nguoiDungRepository.timTheoSoDienThoai(SDT))
                .thenReturn(Optional.of(daLuu(2, VaiTro.USER, TrangThaiTaiKhoan.HOAT_DONG, null)));
        when(passwordHasher.khop("sai-mat-khau", "HASH")).thenReturn(false);

        assertThatThrownBy(() -> authService.dangNhap(new DangNhapCommand(SDT, "sai-mat-khau")))
                .isInstanceOf(SaiThongTinDangNhapException.class);

        verifyNoInteractions(tokenIssuer);
    }

    @Test
    void dangNhapSoDienThoaiKhongTonTai_cungBaoSaiThongTin() {
        when(nguoiDungRepository.timTheoSoDienThoai(SDT)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.dangNhap(new DangNhapCommand(SDT, "matkhau123")))
                .isInstanceOf(SaiThongTinDangNhapException.class);

        verifyNoInteractions(passwordHasher, tokenIssuer);
    }

    @Test
    void dangNhapKhiBiKhoa_biTuChoi() {
        when(nguoiDungRepository.timTheoSoDienThoai(SDT)).thenReturn(Optional.of(
                daLuu(2, VaiTro.USER, TrangThaiTaiKhoan.BI_KHOA, BAY_GIO.plusHours(5))));
        when(passwordHasher.khop("matkhau123", "HASH")).thenReturn(true);

        assertThatThrownBy(() -> authService.dangNhap(new DangNhapCommand(SDT, "matkhau123")))
                .isInstanceOf(TaiKhoanBiKhoaException.class);

        verifyNoInteractions(tokenIssuer);
    }

    @Test
    void dangNhapSauKhiHetKhoa_duocPhep() {
        when(nguoiDungRepository.timTheoSoDienThoai(SDT)).thenReturn(Optional.of(
                daLuu(2, VaiTro.USER, TrangThaiTaiKhoan.BI_KHOA, BAY_GIO.minusMinutes(1))));
        when(passwordHasher.khop("matkhau123", "HASH")).thenReturn(true);
        tokenPhatHanhDuoc(2, VaiTro.USER);

        assertThat(authService.dangNhap(new DangNhapCommand(SDT, "matkhau123")).accessToken()).isEqualTo("TOKEN");
    }
}
