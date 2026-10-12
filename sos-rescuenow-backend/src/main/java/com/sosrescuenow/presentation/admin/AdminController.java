package com.sosrescuenow.presentation.admin;

import com.sosrescuenow.application.admin.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/tho/{id}/duyet")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void duyet(@PathVariable Integer id, @Valid @RequestBody(required = false) DuyetThoRequest request) {
        adminService.duyetTho(id, request == null ? null : request.loaiSuCoIds());
    }
}
