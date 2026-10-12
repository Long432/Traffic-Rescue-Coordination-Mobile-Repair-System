package com.sosrescuenow.application.admin;

import com.sosrescuenow.application.port.NguoiDungRepository;
import com.sosrescuenow.application.port.PasswordHasher;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.model.NguoiDung;
import com.sosrescuenow.domain.model.VaiTro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

/** Tao tai khoan quan tri vien dau tien luc khoi dong (vi API dang ky khong cho tao ADMIN). */
@Service
public class KhoiTaoAdminService {

    private static final Logger log = LoggerFactory.getLogger(KhoiTaoAdminService.class);
    private static final Pattern SO_DIEN_THOAI = Pattern.compile("^0\\d{9}$");
    private static final int DO_DAI_MAT_KHAU_TOI_THIEU = 8;

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordHasher passwordHasher;

    public KhoiTaoAdminService(NguoiDungRepository nguoiDungRepository, PasswordHasher passwordHasher) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.passwordHasher = passwordHasher;
    }

    /**
     * @return true neu vua tao moi; false neu so dien thoai da ton tai (khong ghi de, ke ca mat khau).
     * @throws DuLieuKhongHopLeException neu so dien thoai hoac mat khau khong hop le
     */
    @Transactional
    public boolean taoNeuChuaCo(String soDienThoai, String matKhau) {
        if (soDienThoai == null || !SO_DIEN_THOAI.matcher(soDienThoai).matches()) {
            throw new DuLieuKhongHopLeException("Số điện thoại admin phải gồm 10 chữ số, bắt đầu bằng 0");
        }
        if (matKhau == null || matKhau.length() < DO_DAI_MAT_KHAU_TOI_THIEU || matKhau.length() > 72) {
            throw new DuLieuKhongHopLeException("Mật khẩu admin phải từ 8 đến 72 ký tự");
        }
        if (nguoiDungRepository.tonTaiSoDienThoai(soDienThoai)) {
            log.info("Tài khoản {} đã tồn tại, bỏ qua khởi tạo admin", soDienThoai);
            return false;
        }
        nguoiDungRepository.luu(NguoiDung.moi("Quản trị viên", soDienThoai, passwordHasher.bam(matKhau), VaiTro.ADMIN));
        log.info("Đã tạo tài khoản quản trị viên {}", soDienThoai);
        return true;
    }
}
