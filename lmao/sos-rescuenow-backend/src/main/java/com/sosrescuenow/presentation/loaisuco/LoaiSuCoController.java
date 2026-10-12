package com.sosrescuenow.presentation.loaisuco;

import com.sosrescuenow.application.cuoc.CuocService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loai-su-co")
public class LoaiSuCoController {

    private final CuocService cuocService;

    public LoaiSuCoController(CuocService cuocService) {
        this.cuocService = cuocService;
    }

    /** Danh sach loai su co va gia co ban (can dang nhap). */
    @GetMapping
    public List<LoaiSuCoResponse> danhSach() {
        return cuocService.danhSachLoaiSuCo().stream().map(LoaiSuCoResponse::tu).toList();
    }
}
