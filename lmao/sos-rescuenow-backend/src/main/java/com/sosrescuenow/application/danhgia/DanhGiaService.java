package com.sosrescuenow.application.danhgia;

import com.sosrescuenow.application.auth.AuthenticatedUser;
import com.sosrescuenow.application.port.CuocCuuHoRepository;
import com.sosrescuenow.application.port.DanhGiaRepository;
import com.sosrescuenow.application.port.HoSoThoRepository;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.exception.KhongCoQuyenException;
import com.sosrescuenow.domain.exception.KhongTimThayException;
import com.sosrescuenow.domain.exception.TrangThaiKhongHopLeException;
import com.sosrescuenow.domain.exception.XungDotException;
import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.DanhGia;
import com.sosrescuenow.domain.model.TrangThaiCuoc;
import com.sosrescuenow.domain.model.VaiTro;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Use case: khach danh gia tho sau khi cuoc hoan thanh. Diem trung binh cua tho anh huong thu tu xep hang. */
@Service
public class DanhGiaService {

    private static final Logger log = LoggerFactory.getLogger(DanhGiaService.class);

    private final CuocCuuHoRepository cuocRepository;
    private final DanhGiaRepository danhGiaRepository;
    private final HoSoThoRepository hoSoThoRepository;

    public DanhGiaService(CuocCuuHoRepository cuocRepository,
                          DanhGiaRepository danhGiaRepository,
                          HoSoThoRepository hoSoThoRepository) {
        this.cuocRepository = cuocRepository;
        this.danhGiaRepository = danhGiaRepository;
        this.hoSoThoRepository = hoSoThoRepository;
    }

    @Transactional
    public DanhGia danhGia(Long cuocId, Integer khachId, int soSao, String nhanXet) {
        if (soSao < 1 || soSao > 5) {
            throw new DuLieuKhongHopLeException("Số sao phải từ 1 đến 5");
        }
        CuocCuuHo cuoc = layCuoc(cuocId);
        if (!cuoc.getKhachId().equals(khachId)) {
            throw new KhongCoQuyenException("Bạn không phải chủ của cuốc này");
        }
        if (cuoc.getTrangThai() != TrangThaiCuoc.HOAN_THANH || cuoc.getThoId() == null) {
            throw new TrangThaiKhongHopLeException("Chỉ đánh giá được cuốc đã hoàn thành");
        }
        if (danhGiaRepository.timTheoCuoc(cuocId).isPresent()) {
            throw new XungDotException("Bạn đã đánh giá cuốc này rồi");
        }

        DanhGia moi = new DanhGia(cuocId, soSao, chuanHoa(nhanXet));
        danhGiaRepository.luu(moi);

        // Tinh lai diem trung binh cua tho tu tat ca danh gia (da gom danh gia vua luu).
        List<Integer> tatCa = danhGiaRepository.soSaoCuaTho(cuoc.getThoId());
        double trungBinh = tatCa.stream().mapToInt(Integer::intValue).average().orElse(5.0);
        hoSoThoRepository.datDiemTrungBinh(cuoc.getThoId(), Math.round(trungBinh * 100.0) / 100.0);
        log.info("Khach {} danh gia tho {} {} sao cho cuoc {}", khachId, cuoc.getThoId(), soSao, cuocId);
        return moi;
    }

    /** Chi chu cuoc, tho phu trach hoac admin moi xem duoc. Rong neu chua danh gia. */
    @Transactional(readOnly = true)
    public Optional<DanhGia> xemDanhGia(Long cuocId, AuthenticatedUser nguoiGoi) {
        CuocCuuHo cuoc = layCuoc(cuocId);
        boolean laChu = nguoiGoi.vaiTro() == VaiTro.USER && cuoc.getKhachId().equals(nguoiGoi.nguoiDungId());
        boolean laTho = nguoiGoi.vaiTro() == VaiTro.THO && nguoiGoi.nguoiDungId().equals(cuoc.getThoId());
        if (!laChu && !laTho && nguoiGoi.vaiTro() != VaiTro.ADMIN) {
            throw new KhongCoQuyenException("Bạn không có quyền xem đánh giá này");
        }
        return danhGiaRepository.timTheoCuoc(cuocId);
    }

    private CuocCuuHo layCuoc(Long cuocId) {
        return cuocRepository.timTheoId(cuocId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy cuốc cứu hộ"));
    }

    private static String chuanHoa(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
