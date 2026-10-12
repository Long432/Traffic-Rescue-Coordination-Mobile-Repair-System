package com.sosrescuenow.presentation.error;

import com.sosrescuenow.domain.exception.DomainException;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.exception.KhongCoQuyenException;
import com.sosrescuenow.domain.exception.KhongTimThayException;
import com.sosrescuenow.domain.exception.SaiThongTinDangNhapException;
import com.sosrescuenow.domain.exception.TaiKhoanBiKhoaException;
import com.sosrescuenow.domain.exception.TrangThaiKhongHopLeException;
import com.sosrescuenow.domain.exception.TrungLapException;
import com.sosrescuenow.domain.exception.XungDotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Anh xa exception sang ma HTTP va ApiError. Tat ca loi nghiep vu di qua day. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuLieuKhongHopLeException.class)
    public ResponseEntity<ApiError> duLieuKhongHopLe(DuLieuKhongHopLeException e) {
        return nghiepVu(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler(TrungLapException.class)
    public ResponseEntity<ApiError> trungLap(TrungLapException e) {
        return nghiepVu(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(KhongTimThayException.class)
    public ResponseEntity<ApiError> khongTimThay(KhongTimThayException e) {
        return nghiepVu(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(SaiThongTinDangNhapException.class)
    public ResponseEntity<ApiError> saiThongTinDangNhap(SaiThongTinDangNhapException e) {
        return nghiepVu(HttpStatus.UNAUTHORIZED, e);
    }

    @ExceptionHandler(TaiKhoanBiKhoaException.class)
    public ResponseEntity<ApiError> taiKhoanBiKhoa(TaiKhoanBiKhoaException e) {
        return nghiepVu(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler(XungDotException.class)
    public ResponseEntity<ApiError> xungDot(XungDotException e) {
        return nghiepVu(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(TrangThaiKhongHopLeException.class)
    public ResponseEntity<ApiError> trangThaiKhongHopLe(TrangThaiKhongHopLeException e) {
        return nghiepVu(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(KhongCoQuyenException.class)
    public ResponseEntity<ApiError> khongCoQuyenNghiepVu(KhongCoQuyenException e) {
        return nghiepVu(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> duLieuGuiLenSai(MethodArgumentNotValidException e) {
        Map<String, String> chiTiet = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(loi ->
                chiTiet.putIfAbsent(loi.getField(),
                        loi.getDefaultMessage() != null ? loi.getDefaultMessage() : "Giá trị không hợp lệ"));
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "DU_LIEU_KHONG_HOP_LE", "Dữ liệu gửi lên không hợp lệ", chiTiet));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> jsonSai(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(
                ApiError.of(400, "DU_LIEU_KHONG_HOP_LE", "Nội dung gửi lên không đúng định dạng JSON hoặc có giá trị không hợp lệ"));
    }

    /** Phai khai bao rieng, neu khong handler Exception ben duoi se bien 403 thanh 500. */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> khongCoQuyen(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiError.of(403, "KHONG_CO_QUYEN", "Bạn không có quyền thực hiện thao tác này"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> khongCoDuongDan(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiError.of(404, "KHONG_TIM_THAY", "Đường dẫn không tồn tại"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> saiPhuongThuc(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                ApiError.of(405, "PHUONG_THUC_KHONG_DUOC_HO_TRO", "Phương thức HTTP không được hỗ trợ"));
    }

    /** Loi khong luong truoc: ghi log day du, tra thong bao chung (khong lo chi tiet noi bo). */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> loiHeThong(Exception e) {
        log.error("Lỗi hệ thống không xác định", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiError.of(500, "LOI_HE_THONG", "Đã xảy ra lỗi hệ thống, vui lòng thử lại sau"));
    }

    private ResponseEntity<ApiError> nghiepVu(HttpStatus status, DomainException e) {
        return ResponseEntity.status(status).body(ApiError.of(status.value(), e.getMaLoi(), e.getMessage()));
    }
}
