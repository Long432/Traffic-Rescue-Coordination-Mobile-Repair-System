package com.sosrescuenow.application.cuoc;

import com.sosrescuenow.application.port.CuocCuuHoRepository;
import com.sosrescuenow.application.port.HoSoThoRepository;
import com.sosrescuenow.application.port.LichSuTrangThaiRepository;
import com.sosrescuenow.application.port.ViTriThoRepository;
import com.sosrescuenow.domain.dieuphoi.QuyTacPhatCuoc;
import com.sosrescuenow.domain.dieuphoi.UngVienDaXepHang;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.exception.KhongCoQuyenException;
import com.sosrescuenow.domain.exception.KhongTimThayException;
import com.sosrescuenow.domain.exception.TrangThaiKhongHopLeException;
import com.sosrescuenow.domain.exception.XungDotException;
import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.HoSoTho;
import com.sosrescuenow.domain.model.TrangThaiCuoc;
import com.sosrescuenow.domain.model.ViTri;
import com.sosrescuenow.domain.model.ViTriTho;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/** Use case phia tho: online/offline, gui vi tri, xem cuoc quanh day, nhan cuoc, cap nhat tien do, tra cuoc. */
@Service
public class ThoCuocService {

    private static final Logger log = LoggerFactory.getLogger(ThoCuocService.class);

    /** Cac trang thai tho duoc phep tu chuyen toi (nhan cuoc, huy do cac ham rieng dam nhiem). */
    private static final Set<TrangThaiCuoc> TRANG_THAI_THO_DUOC_CHUYEN = EnumSet.of(
            TrangThaiCuoc.DANG_DEN, TrangThaiCuoc.DA_DEN, TrangThaiCuoc.DANG_SUA,
            TrangThaiCuoc.CHO_THANH_TOAN, TrangThaiCuoc.HOAN_THANH);

    /** Tho phai o trong ban kinh nay quanh khach moi duoc bam "da den". */
    static final double BAN_KINH_CHECK_IN_MET = 100.0;

    private final HoSoThoRepository hoSoThoRepository;
    private final ViTriThoRepository viTriThoRepository;
    private final CuocCuuHoRepository cuocRepository;
    private final LichSuTrangThaiRepository lichSuRepository;
    private final TimThoService timThoService;
    private final Clock clock;

    public ThoCuocService(HoSoThoRepository hoSoThoRepository,
                          ViTriThoRepository viTriThoRepository,
                          CuocCuuHoRepository cuocRepository,
                          LichSuTrangThaiRepository lichSuRepository,
                          TimThoService timThoService,
                          Clock clock) {
        this.hoSoThoRepository = hoSoThoRepository;
        this.viTriThoRepository = viTriThoRepository;
        this.cuocRepository = cuocRepository;
        this.lichSuRepository = lichSuRepository;
        this.timThoService = timThoService;
        this.clock = clock;
    }

    // ---------------- Trang thai va vi tri ----------------

    /** Ho so cua chinh tho (app doc luc khoi dong de biet online/ban/da duyet). */
    @Transactional(readOnly = true)
    public HoSoTho hoSo(Integer thoId) {
        return layHoSo(thoId);
    }

    /** Cuoc tho dang lam (de app tiep tuc sau khi mo lai); rong neu dang ranh. */
    @Transactional(readOnly = true)
    public Optional<CuocCuuHo> cuocHienTai(Integer thoId) {
        layHoSo(thoId);
        return cuocRepository.timCuocDangLamCuaTho(thoId);
    }

    @Transactional
    public void datTrangThaiOnline(Integer thoId, boolean online) {
        HoSoTho tho = layHoSo(thoId);
        if (online && !tho.daDuyet()) {
            throw new KhongCoQuyenException("Hồ sơ thợ của bạn chưa được duyệt");
        }
        if (!online && tho.dangBan()) {
            throw new TrangThaiKhongHopLeException("Bạn đang có cuốc chưa hoàn thành, không thể chuyển sang offline");
        }
        hoSoThoRepository.datTrangThaiOnline(thoId, online);
    }

    @Transactional
    public void capNhatViTri(Integer thoId, double latitude, double longitude) {
        layHoSo(thoId);
        viTriThoRepository.luu(thoId, new ViTri(latitude, longitude), LocalDateTime.now(clock));
    }

    // ---------------- Xem va nhan cuoc ----------------

    /** Cac cuoc dang tim ma tho nay duoc phep thay luc nay, gan nhat truoc. */
    @Transactional(readOnly = true)
    public List<CuocQuanhToi> cuocQuanhToi(Integer thoId) {
        LocalDateTime bayGio = LocalDateTime.now(clock);
        HoSoTho tho = layHoSo(thoId);
        if (!tho.sanSang()) {
            return List.of();
        }
        Optional<ViTriTho> viTriTho = viTriThoRepository.timTheoThoId(thoId)
                .filter(v -> viTriConMoi(v, bayGio));
        if (viTriTho.isEmpty()) {
            return List.of();
        }
        ViTri viTri = viTriTho.get().viTri();
        LocalDateTime moc = bayGio.minus(QuyTacPhatCuoc.THOI_GIAN_HET_HAN);
        return cuocRepository.timDangTimBatDauSau(moc).stream()
                .filter(cuoc -> duocPhepNhan(tho, viTri, cuoc, bayGio))
                .map(cuoc -> new CuocQuanhToi(cuoc, viTri.khoangCachKm(cuoc.getViTri())))
                .sorted(Comparator.comparingDouble(CuocQuanhToi::khoangCachKm))
                .toList();
    }

    /**
     * Tho bam "Nhan". Chong hai tho cung nhan mot cuoc bang cau UPDATE co dieu kien
     * (WHERE TrangThai = 'DANG_TIM'): chi mot nguoi cap nhat duoc 1 dong, nguoi con lai nhan 0 dong.
     */
    @Transactional
    public CuocCuuHo nhanCuoc(Integer thoId, Long cuocId) {
        LocalDateTime bayGio = LocalDateTime.now(clock);
        HoSoTho tho = layHoSo(thoId);
        if (!tho.daDuyet()) {
            throw new KhongCoQuyenException("Hồ sơ thợ của bạn chưa được duyệt");
        }
        if (!tho.online()) {
            throw new TrangThaiKhongHopLeException("Bạn đang offline, hãy bật trạng thái sẵn sàng trước");
        }
        if (tho.dangBan()) {
            throw new XungDotException("Bạn đang có cuốc khác chưa hoàn thành");
        }
        CuocCuuHo cuoc = layCuoc(cuocId);
        if (cuoc.getTrangThai() != TrangThaiCuoc.DANG_TIM) {
            throw new XungDotException("Cuốc xe đã được người khác nhận hoặc không còn nhận được");
        }
        ViTriTho viTriTho = viTriThoRepository.timTheoThoId(thoId)
                .filter(v -> viTriConMoi(v, bayGio))
                .orElseThrow(() -> new DuLieuKhongHopLeException("Chưa có vị trí mới của bạn, hãy bật định vị"));
        if (!duocPhepNhan(tho, viTriTho.viTri(), cuoc, bayGio)) {
            throw new KhongCoQuyenException("Cuốc này hiện không dành cho bạn (quá xa, không phù hợp loại sự cố, "
                    + "hoặc đang trong thời gian ưu tiên thợ khác)");
        }

        // Buoc 1: danh dau tho ban (nguyen tu). Buoc 2: gan cuoc (nguyen tu). Neu buoc 2 that bai
        // thi tra tho ve trang thai ranh.
        if (!hoSoThoRepository.datBanNeuRanh(thoId)) {
            throw new XungDotException("Bạn đang bận hoặc không ở trạng thái sẵn sàng");
        }
        if (!cuocRepository.nhanCuoc(cuocId, thoId)) {
            hoSoThoRepository.datRanh(thoId);
            throw new XungDotException("Cuốc xe đã được người khác nhận");
        }
        lichSuRepository.ghi(cuocId, TrangThaiCuoc.DANG_TIM, TrangThaiCuoc.DA_NHAN, thoId, null);
        log.info("Thợ {} nhận cuốc {}", thoId, cuocId);
        return layCuoc(cuocId);
    }

    // ---------------- Cap nhat tien do ----------------

    /** Tho cap nhat tien do: DANG_DEN, DA_DEN (check-in gan khach), DANG_SUA, CHO_THANH_TOAN, HOAN_THANH. */
    @Transactional
    public CuocCuuHo chuyenTrangThai(Integer thoId, Long cuocId, TrangThaiCuoc den) {
        LocalDateTime bayGio = LocalDateTime.now(clock);
        CuocCuuHo cuoc = layCuocCuaTho(thoId, cuocId);
        if (!TRANG_THAI_THO_DUOC_CHUYEN.contains(den)) {
            throw new DuLieuKhongHopLeException(
                    "Thợ chỉ được chuyển sang: DANG_DEN, DA_DEN, DANG_SUA, CHO_THANH_TOAN, HOAN_THANH");
        }
        TrangThaiCuoc tu = cuoc.getTrangThai();
        if (!tu.coTheChuyenSang(den)) {
            throw new TrangThaiKhongHopLeException("Không thể chuyển từ " + tu + " sang " + den);
        }
        if (den == TrangThaiCuoc.DA_DEN) {
            kiemTraDaDenGanKhach(thoId, cuoc, bayGio);
        }

        boolean thanhCong = den == TrangThaiCuoc.CHO_THANH_TOAN
                ? cuocRepository.chuyenSangChoThanhToan(cuocId, cuoc.getGiaUocTinh())
                : cuocRepository.chuyenTrangThai(cuocId, tu, den);
        if (!thanhCong) {
            throw new XungDotException("Trạng thái cuốc vừa thay đổi, vui lòng tải lại");
        }
        if (den == TrangThaiCuoc.HOAN_THANH) {
            hoSoThoRepository.datRanh(thoId);
        }
        lichSuRepository.ghi(cuocId, tu, den, thoId, null);
        return layCuoc(cuocId);
    }

    /** Tho tra lai cuoc (truoc khi den noi): cuoc quay ve DANG_TIM de tim tho khac. */
    @Transactional
    public CuocCuuHo thoTraCuoc(Integer thoId, Long cuocId, String lyDo) {
        CuocCuuHo cuoc = layCuocCuaTho(thoId, cuocId);
        TrangThaiCuoc tu = cuoc.getTrangThai();
        if ((tu != TrangThaiCuoc.DA_NHAN && tu != TrangThaiCuoc.DANG_DEN)
                || !tu.coTheChuyenSang(TrangThaiCuoc.DANG_TIM)) {
            throw new TrangThaiKhongHopLeException("Chỉ được trả cuốc trước khi đến nơi (đang ở " + tu + ")");
        }
        String lyDoChuan = lyDo == null || lyDo.isBlank() ? "Thợ trả cuốc" : lyDo.trim();
        if (!cuocRepository.thoTraCuoc(cuocId, thoId, tu, LocalDateTime.now(clock))) {
            throw new XungDotException("Trạng thái cuốc vừa thay đổi, vui lòng tải lại");
        }
        hoSoThoRepository.datRanh(thoId);
        lichSuRepository.ghi(cuocId, tu, TrangThaiCuoc.DANG_TIM, thoId, lyDoChuan);
        log.info("Thợ {} trả cuốc {}: {}", thoId, cuocId, lyDoChuan);
        return layCuoc(cuocId);
    }

    // ---------------- Ham noi bo ----------------

    /** Tho co duoc phep thay/nhan cuoc nay o thoi diem hien tai khong (theo QuyTacPhatCuoc). */
    private boolean duocPhepNhan(HoSoTho tho, ViTri viTriTho, CuocCuuHo cuoc, LocalDateTime bayGio) {
        if (!tho.sanSang() || !tho.lamDuoc(cuoc.getLoaiSuCoId())) {
            return false;
        }
        QuyTacPhatCuoc.GiaiDoan giaiDoan = QuyTacPhatCuoc.tai(cuoc.tuoiTimKiem(bayGio));
        if (giaiDoan.hetHan()) {
            return false;
        }
        if (viTriTho.khoangCachKm(cuoc.getViTri()) > giaiDoan.banKinhKm()) {
            return false;
        }
        if (giaiDoan.soThoUuTien() == null) {
            return true;
        }
        return timThoService.xepHang(cuoc.getViTri(), cuoc.getLoaiSuCoId(), giaiDoan.banKinhKm(), bayGio).stream()
                .limit(giaiDoan.soThoUuTien())
                .map(UngVienDaXepHang::ungVien)
                .anyMatch(u -> u.thoId().equals(tho.id()));
    }

    private void kiemTraDaDenGanKhach(Integer thoId, CuocCuuHo cuoc, LocalDateTime bayGio) {
        ViTriTho viTriTho = viTriThoRepository.timTheoThoId(thoId)
                .filter(v -> viTriConMoi(v, bayGio))
                .orElseThrow(() -> new DuLieuKhongHopLeException("Chưa có vị trí mới của bạn, hãy bật định vị"));
        double met = viTriTho.viTri().khoangCachKm(cuoc.getViTri()) * 1000.0;
        if (met > BAN_KINH_CHECK_IN_MET) {
            throw new DuLieuKhongHopLeException(String.format(Locale.ROOT,
                    "Bạn chưa đến gần vị trí khách (còn khoảng %.0f m, cần trong vòng %.0f m)",
                    met, BAN_KINH_CHECK_IN_MET));
        }
    }

    private static boolean viTriConMoi(ViTriTho viTri, LocalDateTime bayGio) {
        return !viTri.capNhatLuc().isBefore(bayGio.minus(QuyTacPhatCuoc.THOI_GIAN_VI_TRI_HOP_LE));
    }

    private CuocCuuHo layCuocCuaTho(Integer thoId, Long cuocId) {
        CuocCuuHo cuoc = layCuoc(cuocId);
        if (!Objects.equals(cuoc.getThoId(), thoId)) {
            throw new KhongCoQuyenException("Cuốc này không được giao cho bạn");
        }
        return cuoc;
    }

    private HoSoTho layHoSo(Integer thoId) {
        return hoSoThoRepository.timHoSo(thoId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy hồ sơ thợ"));
    }

    private CuocCuuHo layCuoc(Long cuocId) {
        return cuocRepository.timTheoId(Objects.requireNonNull(cuocId, "cuocId"))
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy cuốc cứu hộ"));
    }
}
