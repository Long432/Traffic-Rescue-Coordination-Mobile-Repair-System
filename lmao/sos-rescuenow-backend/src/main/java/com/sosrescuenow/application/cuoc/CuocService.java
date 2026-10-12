package com.sosrescuenow.application.cuoc;

import com.sosrescuenow.application.auth.AuthenticatedUser;
import com.sosrescuenow.application.port.CuocCuuHoRepository;
import com.sosrescuenow.application.port.HoSoThoRepository;
import com.sosrescuenow.application.port.LichSuTrangThaiRepository;
import com.sosrescuenow.application.port.LoaiSuCoRepository;
import com.sosrescuenow.application.port.NguoiDungRepository;
import com.sosrescuenow.application.port.ViTriThoRepository;
import com.sosrescuenow.domain.dieuphoi.BangGia;
import com.sosrescuenow.domain.dieuphoi.QuyTacPhatCuoc;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.exception.KhongCoQuyenException;
import com.sosrescuenow.domain.exception.KhongTimThayException;
import com.sosrescuenow.domain.exception.TrangThaiKhongHopLeException;
import com.sosrescuenow.domain.exception.XungDotException;
import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.LoaiSuCo;
import com.sosrescuenow.domain.model.NguoiDung;
import com.sosrescuenow.domain.model.TrangThaiCuoc;
import com.sosrescuenow.domain.model.VaiTro;
import com.sosrescuenow.domain.model.ViTri;
import com.sosrescuenow.domain.model.ViTriTho;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Use case phia khach hang: gui SOS, xem, huy cuoc; va don dep cuoc het han. */
@Service
public class CuocService {

    private static final Logger log = LoggerFactory.getLogger(CuocService.class);

    private final LoaiSuCoRepository loaiSuCoRepository;
    private final CuocCuuHoRepository cuocRepository;
    private final LichSuTrangThaiRepository lichSuRepository;
    private final HoSoThoRepository hoSoThoRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ViTriThoRepository viTriThoRepository;
    private final TimThoService timThoService;
    private final Clock clock;

    public CuocService(LoaiSuCoRepository loaiSuCoRepository,
                       CuocCuuHoRepository cuocRepository,
                       LichSuTrangThaiRepository lichSuRepository,
                       HoSoThoRepository hoSoThoRepository,
                       NguoiDungRepository nguoiDungRepository,
                       ViTriThoRepository viTriThoRepository,
                       TimThoService timThoService,
                       Clock clock) {
        this.loaiSuCoRepository = loaiSuCoRepository;
        this.cuocRepository = cuocRepository;
        this.lichSuRepository = lichSuRepository;
        this.hoSoThoRepository = hoSoThoRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.viTriThoRepository = viTriThoRepository;
        this.timThoService = timThoService;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<LoaiSuCo> danhSachLoaiSuCo() {
        return loaiSuCoRepository.timTatCa();
    }

    @Transactional
    public KetQuaGuiSos guiSos(GuiSosCommand lenh) {
        ViTri viTri = new ViTri(lenh.latitude(), lenh.longitude()); // tu kiem tra toa do
        LoaiSuCo loaiSuCo = loaiSuCoRepository.timTheoId(lenh.loaiSuCoId())
                .orElseThrow(() -> new DuLieuKhongHopLeException("Loại sự cố không tồn tại"));
        if (cuocRepository.demDangHoatDong(lenh.khachId()) > 0) {
            throw new XungDotException("Bạn đang có một yêu cầu cứu hộ chưa hoàn tất");
        }

        LocalDateTime bayGio = LocalDateTime.now(clock);
        BigDecimal giaUocTinh = BangGia.tinhGia(loaiSuCo.giaCoBan(), bayGio);
        CuocCuuHo daLuu = cuocRepository.luu(CuocCuuHo.moi(lenh.khachId(), loaiSuCo.id(), viTri,
                chuanHoa(lenh.diaChi()), chuanHoa(lenh.ghiChu()), giaUocTinh, bayGio));
        lichSuRepository.ghi(daLuu.getId(), null, TrangThaiCuoc.DANG_TIM, lenh.khachId(), null);

        int soTho = timThoService.xepHang(viTri, loaiSuCo.id(), QuyTacPhatCuoc.BAN_KINH_CO_BAN_KM, bayGio).size();
        log.info("Cuộc cứu hộ mới: id={}, khachId={}, loaiSuCoId={}, soThoPhuHop={}",
                daLuu.getId(), lenh.khachId(), loaiSuCo.id(), soTho);
        return new KetQuaGuiSos(daLuu, soTho);
    }

    /** Chi chu cuoc, tho duoc giao, hoac admin moi duoc xem. */
    @Transactional(readOnly = true)
    public CuocCuuHo xemCuoc(Long cuocId, AuthenticatedUser nguoiGoi) {
        CuocCuuHo cuoc = layCuoc(cuocId);
        boolean laChu = nguoiGoi.vaiTro() == VaiTro.USER && cuoc.getKhachId().equals(nguoiGoi.nguoiDungId());
        boolean laThoDuocGiao = nguoiGoi.vaiTro() == VaiTro.THO && nguoiGoi.nguoiDungId().equals(cuoc.getThoId());
        if (!laChu && !laThoDuocGiao && nguoiGoi.vaiTro() != VaiTro.ADMIN) {
            throw new KhongCoQuyenException("Bạn không có quyền xem cuốc này");
        }
        return cuoc;
    }

    /**
     * Tho dang phu trach cuoc (ten va vi tri moi nhat) de khach theo doi tren ban do.
     * Chi chu cuoc moi xem duoc; rong neu chua co tho hoac cuoc da ket thuc.
     */
    @Transactional(readOnly = true)
    public Optional<ThoTrenCuoc> thoCuaCuoc(Long cuocId, Integer khachId) {
        CuocCuuHo cuoc = layCuoc(cuocId);
        if (!cuoc.getKhachId().equals(khachId)) {
            throw new KhongCoQuyenException("Bạn không phải chủ của cuốc này");
        }
        if (cuoc.getThoId() == null || cuoc.getTrangThai().laKetThuc()) {
            return Optional.empty();
        }
        String hoTen = nguoiDungRepository.timTheoId(cuoc.getThoId()).map(NguoiDung::getHoTen).orElse("Thợ");
        Optional<ViTriTho> viTri = viTriThoRepository.timTheoThoId(cuoc.getThoId());
        return Optional.of(new ThoTrenCuoc(cuoc.getThoId(), hoTen,
                viTri.map(ViTriTho::viTri).orElse(null), viTri.map(ViTriTho::capNhatLuc).orElse(null)));
    }

    @Transactional(readOnly = true)
    public List<CuocCuuHo> cuocCuaToi(Integer khachId) {
        return cuocRepository.timCuaKhach(khachId);
    }

    /** Khach huy cuoc. Chi huy duoc khi may trang thai cho phep (truoc khi tho bat dau sua). */
    @Transactional
    public CuocCuuHo huyCuoc(Long cuocId, Integer khachId, String lyDo) {
        CuocCuuHo cuoc = layCuoc(cuocId);
        if (!cuoc.getKhachId().equals(khachId)) {
            throw new KhongCoQuyenException("Bạn không phải chủ của cuốc này");
        }
        TrangThaiCuoc tu = cuoc.getTrangThai();
        if (!tu.coTheChuyenSang(TrangThaiCuoc.HUY)) {
            throw new TrangThaiKhongHopLeException("Không thể hủy cuốc đang ở trạng thái " + tu);
        }
        String lyDoChuan = lyDo == null || lyDo.isBlank() ? "Khách hủy" : lyDo.trim();
        if (!cuocRepository.huyCuoc(cuocId, tu, lyDoChuan)) {
            throw new XungDotException("Trạng thái cuốc vừa thay đổi, vui lòng thử lại");
        }
        if (cuoc.getThoId() != null) {
            hoSoThoRepository.datRanh(cuoc.getThoId());
        }
        lichSuRepository.ghi(cuocId, tu, TrangThaiCuoc.HUY, khachId, lyDoChuan);
        return layCuoc(cuocId);
    }

    /** Chuyen cac cuoc tim qua lau (khong co tho) sang KHONG_CO_THO. Goi dinh ky boi job. */
    @Transactional
    public int xuLyCuocHetHan() {
        LocalDateTime moc = LocalDateTime.now(clock).minus(QuyTacPhatCuoc.THOI_GIAN_HET_HAN);
        int daXuLy = 0;
        for (CuocCuuHo cuoc : cuocRepository.timDangTimBatDauTruoc(moc)) {
            if (cuocRepository.chuyenTrangThai(cuoc.getId(), TrangThaiCuoc.DANG_TIM, TrangThaiCuoc.KHONG_CO_THO)) {
                lichSuRepository.ghi(cuoc.getId(), TrangThaiCuoc.DANG_TIM, TrangThaiCuoc.KHONG_CO_THO,
                        null, "Hết thời gian tìm thợ");
                daXuLy++;
            }
        }
        return daXuLy;
    }

    private CuocCuuHo layCuoc(Long cuocId) {
        return cuocRepository.timTheoId(Objects.requireNonNull(cuocId, "cuocId"))
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy cuốc cứu hộ"));
    }

    private static String chuanHoa(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
