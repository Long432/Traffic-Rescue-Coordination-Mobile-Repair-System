package com.sosrescuenow.presentation.cuoc;

import com.sosrescuenow.application.auth.AuthenticatedUser;
import com.sosrescuenow.application.cuoc.CuocService;
import com.sosrescuenow.application.cuoc.GuiSosCommand;
import com.sosrescuenow.application.danhgia.DanhGiaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** API cuoc cuu ho cho khach hang. */
@RestController
@RequestMapping("/api/cuoc")
public class CuocController {

    private final CuocService cuocService;
    private final DanhGiaService danhGiaService;

    public CuocController(CuocService cuocService, DanhGiaService danhGiaService) {
        this.cuocService = cuocService;
        this.danhGiaService = danhGiaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public GuiSosResponse guiSos(@AuthenticationPrincipal AuthenticatedUser nguoiDung,
                                 @Valid @RequestBody GuiSosRequest request) {
        return GuiSosResponse.tu(cuocService.guiSos(new GuiSosCommand(
                nguoiDung.nguoiDungId(), request.loaiSuCoId(), request.latitude(), request.longitude(),
                request.diaChi(), request.ghiChu())));
    }

    @GetMapping("/cua-toi")
    @PreAuthorize("hasRole('USER')")
    public List<CuocResponse> cuaToi(@AuthenticationPrincipal AuthenticatedUser nguoiDung) {
        return cuocService.cuocCuaToi(nguoiDung.nguoiDungId()).stream().map(CuocResponse::tu).toList();
    }

    /** Chu cuoc, tho duoc giao hoac admin moi xem duoc. */
    @GetMapping("/{id}")
    public CuocResponse xem(@AuthenticationPrincipal AuthenticatedUser nguoiDung, @PathVariable Long id) {
        return CuocResponse.tu(cuocService.xemCuoc(id, nguoiDung));
    }

    /** Ten va vi tri thoi gian gan-thuc cua tho dang phu trach cuoc; 204 neu chua co tho hoac cuoc da ket thuc. */
    @GetMapping("/{id}/vi-tri-tho")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ThoTrenCuocResponse> viTriTho(@AuthenticationPrincipal AuthenticatedUser nguoiDung,
                                                        @PathVariable Long id) {
        return cuocService.thoCuaCuoc(id, nguoiDung.nguoiDungId())
                .map(t -> ResponseEntity.ok(ThoTrenCuocResponse.tu(t)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /** Khach danh gia tho sau khi cuoc hoan thanh (moi cuoc mot lan). */
    @PostMapping("/{id}/danh-gia")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('USER')")
    public DanhGiaResponse danhGia(@AuthenticationPrincipal AuthenticatedUser nguoiDung, @PathVariable Long id,
                                   @Valid @RequestBody DanhGiaRequest request) {
        return DanhGiaResponse.tu(danhGiaService.danhGia(id, nguoiDung.nguoiDungId(), request.soSao(), request.nhanXet()));
    }

    /** Xem danh gia cua cuoc; 204 neu chua co. */
    @GetMapping("/{id}/danh-gia")
    public ResponseEntity<DanhGiaResponse> xemDanhGia(@AuthenticationPrincipal AuthenticatedUser nguoiDung,
                                                      @PathVariable Long id) {
        return danhGiaService.xemDanhGia(id, nguoiDung)
                .map(d -> ResponseEntity.ok(DanhGiaResponse.tu(d)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/{id}/huy")
    @PreAuthorize("hasRole('USER')")
    public CuocResponse huy(@AuthenticationPrincipal AuthenticatedUser nguoiDung, @PathVariable Long id,
                            @Valid @RequestBody(required = false) HuyCuocRequest request) {
        String lyDo = request == null ? null : request.lyDo();
        return CuocResponse.tu(cuocService.huyCuoc(id, nguoiDung.nguoiDungId(), lyDo));
    }
}
