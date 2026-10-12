package com.sosrescuenow.presentation.admin;

import com.sosrescuenow.application.admin.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;

/** API danh cho quan tri vien. Chi tai khoan vai tro ADMIN goi duoc. */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/tho")
    public List<ThoTomTatResponse> danhSachTho(@RequestParam(defaultValue = "false") boolean chiChuaDuyet) {
        return adminService.danhSachTho(chiChuaDuyet).stream().map(ThoTomTatResponse::tu).toList();
    }

    /**
     * Duyet tho. Khong co tham so {@code loaiSuCoIds}: cho lam moi loai su co.
     * Vi du chi cho loai 1 va 2: POST /api/admin/tho/5/duyet?loaiSuCoIds=1,2
     * (Dung query param thay vi body de goi duoc tu moi cong cu, ke ca khi khong co Content-Type.)
     */
    @PostMapping("/tho/{id}/duyet")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void duyet(@PathVariable Integer id,
                      @RequestParam(name = "loaiSuCoIds", required = false) List<Integer> loaiSuCoIds) {
        adminService.duyetTho(id, loaiSuCoIds == null ? null : new HashSet<>(loaiSuCoIds));
    }
}
