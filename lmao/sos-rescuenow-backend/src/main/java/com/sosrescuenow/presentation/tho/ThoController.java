package com.sosrescuenow.presentation.tho;

import com.sosrescuenow.application.auth.AuthenticatedUser;
import com.sosrescuenow.application.cuoc.ThoCuocService;
import com.sosrescuenow.presentation.cuoc.CuocResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** API danh cho tho cuu ho. Chi tai khoan vai tro THO moi goi duoc. */
@RestController
@RequestMapping("/api/tho")
@PreAuthorize("hasRole('THO')")
public class ThoController {

    private final ThoCuocService thoCuocService;

    public ThoController(ThoCuocService thoCuocService) {
        this.thoCuocService = thoCuocService;
    }

    @GetMapping("/ho-so")
    public HoSoThoResponse hoSo(@AuthenticationPrincipal AuthenticatedUser tho) {
        return HoSoThoResponse.tu(thoCuocService.hoSo(tho.nguoiDungId()));
    }

    /** Cuoc tho dang lam; 204 neu dang ranh. */
    @GetMapping("/cuoc-hien-tai")
    public ResponseEntity<CuocResponse> cuocHienTai(@AuthenticationPrincipal AuthenticatedUser tho) {
        return thoCuocService.cuocHienTai(tho.nguoiDungId())
                .map(c -> ResponseEntity.ok(CuocResponse.tu(c)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping("/trang-thai")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void datTrangThai(@AuthenticationPrincipal AuthenticatedUser tho,
                             @Valid @RequestBody TrangThaiOnlineRequest request) {
        thoCuocService.datTrangThaiOnline(tho.nguoiDungId(), request.online());
    }

    /** App tho goi dinh ky (3-5 giay/lan khi dang online). */
    @PostMapping("/vi-tri")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void capNhatViTri(@AuthenticationPrincipal AuthenticatedUser tho,
                             @Valid @RequestBody ViTriRequest request) {
        thoCuocService.capNhatViTri(tho.nguoiDungId(), request.latitude(), request.longitude());
    }

    @GetMapping("/cuoc-quanh-toi")
    public List<CuocQuanhToiResponse> cuocQuanhToi(@AuthenticationPrincipal AuthenticatedUser tho) {
        return thoCuocService.cuocQuanhToi(tho.nguoiDungId()).stream().map(CuocQuanhToiResponse::tu).toList();
    }

    @PostMapping("/cuoc/{id}/nhan")
    public CuocResponse nhan(@AuthenticationPrincipal AuthenticatedUser tho, @PathVariable Long id) {
        return CuocResponse.tu(thoCuocService.nhanCuoc(tho.nguoiDungId(), id));
    }

    @PostMapping("/cuoc/{id}/trang-thai")
    public CuocResponse chuyenTrangThai(@AuthenticationPrincipal AuthenticatedUser tho, @PathVariable Long id,
                                        @Valid @RequestBody ChuyenTrangThaiRequest request) {
        return CuocResponse.tu(thoCuocService.chuyenTrangThai(tho.nguoiDungId(), id, request.trangThai()));
    }

    @PostMapping("/cuoc/{id}/tra-cuoc")
    public CuocResponse traCuoc(@AuthenticationPrincipal AuthenticatedUser tho, @PathVariable Long id,
                                @Valid @RequestBody TraCuocRequest request) {
        return CuocResponse.tu(thoCuocService.thoTraCuoc(tho.nguoiDungId(), id, request.lyDo()));
    }
}
