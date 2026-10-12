package com.sosrescuenow.application.auth;

import com.sosrescuenow.application.port.HoSoThoRepository;
import com.sosrescuenow.application.port.NguoiDungRepository;
import com.sosrescuenow.application.port.PasswordHasher;
import com.sosrescuenow.application.port.TokenIssuer;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.exception.SaiThongTinDangNhapException;
import com.sosrescuenow.domain.exception.TaiKhoanBiKhoaException;
import com.sosrescuenow.domain.exception.TrungLapException;
import com.sosrescuenow.domain.model.NguoiDung;
import com.sosrescuenow.domain.model.VaiTro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

/** Use case: dang ky va dang nhap. Chi phu thuoc vao cac cong (port) va domain. */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final NguoiDungRepository nguoiDungRepository;
    private final HoSoThoRepository hoSoThoRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;
    private final Clock clock;

    public AuthService(NguoiDungRepository nguoiDungRepository,
                       HoSoThoRepository hoSoThoRepository,
                       PasswordHasher passwordHasher,
                       TokenIssuer tokenIssuer,
                       Clock clock) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.hoSoThoRepository = hoSoThoRepository;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
        this.clock = clock;
    }

    /** Dang ky tai khoan USER hoac THO. ADMIN khong the tu dang ky. */
    @Transactional
    public AuthResult dangKy(DangKyCommand lenh) {
        if (lenh.vaiTro() != VaiTro.USER && lenh.vaiTro() != VaiTro.THO) {
            throw new DuLieuKhongHopLeException("Chỉ được đăng ký tài khoản khách hàng (USER) hoặc thợ (THO)");
        }
        if (nguoiDungRepository.tonTaiSoDienThoai(lenh.soDienThoai())) {
            throw new TrungLapException("Số điện thoại đã được đăng ký");
        }

        NguoiDung moi = NguoiDung.moi(
                lenh.hoTen().trim(),
                lenh.soDienThoai(),
                passwordHasher.bam(lenh.matKhau()),
                lenh.vaiTro());
        NguoiDung daLuu = nguoiDungRepository.luu(moi);

        if (daLuu.getVaiTro() == VaiTro.THO) {
            hoSoThoRepository.taoHoSoMoi(daLuu.getId());
        }
        log.info("Đăng ký tài khoản mới: id={}, vaiTro={}", daLuu.getId(), daLuu.getVaiTro());
        return taoKetQua(daLuu);
    }

    @Transactional(readOnly = true)
    public AuthResult dangNhap(DangNhapCommand lenh) {
        NguoiDung nguoiDung = nguoiDungRepository.timTheoSoDienThoai(lenh.soDienThoai())
                .orElseThrow(SaiThongTinDangNhapException::new);

        // Kiem tra mat khau TRUOC khi bao trang thai khoa, de nguoi khong biet mat khau
        // khong suy ra duoc tai khoan nao dang bi khoa.
        if (!passwordHasher.khop(lenh.matKhau(), nguoiDung.getMatKhauHash())) {
            throw new SaiThongTinDangNhapException();
        }
        if (nguoiDung.dangBiKhoa(LocalDateTime.now(clock))) {
            throw new TaiKhoanBiKhoaException(nguoiDung.getKhoaDen());
        }
        return taoKetQua(nguoiDung);
    }

    private AuthResult taoKetQua(NguoiDung nguoiDung) {
        String token = tokenIssuer.phatHanh(nguoiDung.getId(), nguoiDung.getVaiTro());
        return new AuthResult(
                token,
                tokenIssuer.thoiHan().toSeconds(),
                nguoiDung.getId(),
                nguoiDung.getHoTen(),
                nguoiDung.getVaiTro());
    }
}
