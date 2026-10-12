package com.sosrescuenow.infrastructure.config;

import com.sosrescuenow.application.admin.KhoiTaoAdminService;
import com.sosrescuenow.domain.exception.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Luc khoi dong, neu dat bien moi truong ADMIN_SDT va ADMIN_MAT_KHAU thi tao tai khoan quan tri vien
 * (neu so dien thoai do chua co). Khong dat hai bien nay thi khong lam gi.
 */
@Component
class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final KhoiTaoAdminService khoiTaoAdminService;
    private final String soDienThoai;
    private final String matKhau;

    AdminBootstrapRunner(KhoiTaoAdminService khoiTaoAdminService,
                         @Value("${sos.admin.so-dien-thoai:}") String soDienThoai,
                         @Value("${sos.admin.mat-khau:}") String matKhau) {
        this.khoiTaoAdminService = khoiTaoAdminService;
        this.soDienThoai = soDienThoai;
        this.matKhau = matKhau;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (soDienThoai.isBlank() || matKhau.isBlank()) {
            return;
        }
        try {
            khoiTaoAdminService.taoNeuChuaCo(soDienThoai, matKhau);
        } catch (DomainException e) {
            // Cau hinh sai khong duoc lam hong ca ung dung: chi canh bao.
            log.warn("Không khởi tạo được tài khoản admin: {}", e.getMessage());
        }
    }
}
