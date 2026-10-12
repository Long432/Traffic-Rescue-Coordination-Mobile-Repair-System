package com.sosrescuenow.application.fake;

import com.sosrescuenow.application.port.CuocCuuHoRepository;
import com.sosrescuenow.domain.model.CuocCuuHo;
import com.sosrescuenow.domain.model.TrangThaiCuoc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.UnaryOperator;

/**
 * Ban gia trong bo nho. Cap nhat co dieu kien dung vong lap compare-and-set tren ConcurrentHashMap,
 * gia lap "UPDATE ... WHERE TrangThai = ..." nguyen tu cua CSDL.
 */
public class InMemoryCuocCuuHoRepository implements CuocCuuHoRepository {

    private final ConcurrentHashMap<Long, CuocCuuHo> duLieu = new ConcurrentHashMap<>();
    private final AtomicLong idKeTiep = new AtomicLong(1);

    @Override
    public CuocCuuHo luu(CuocCuuHo c) {
        long id = idKeTiep.getAndIncrement();
        CuocCuuHo daLuu = sao(id, c, c.getThoId(), c.getTrangThai(), c.getTongTien(), c.getLyDoHuy(), c.getBatDauTimLuc());
        duLieu.put(id, daLuu);
        return daLuu;
    }

    /** Them truc tiep mot cuoc co san (dung cho dung du lieu test). */
    public CuocCuuHo them(CuocCuuHo c) {
        return luu(c);
    }

    @Override
    public Optional<CuocCuuHo> timTheoId(Long id) {
        return Optional.ofNullable(duLieu.get(id));
    }

    @Override
    public List<CuocCuuHo> timCuaKhach(Integer khachId) {
        return duLieu.values().stream().filter(c -> c.getKhachId().equals(khachId))
                .sorted(Comparator.comparing(CuocCuuHo::getId).reversed()).toList();
    }

    @Override
    public long demDangHoatDong(Integer khachId) {
        return duLieu.values().stream().filter(c -> c.getKhachId().equals(khachId) && c.dangHoatDong()).count();
    }

    @Override
    public List<CuocCuuHo> timDangTimBatDauSau(LocalDateTime moc) {
        return duLieu.values().stream()
                .filter(c -> c.getTrangThai() == TrangThaiCuoc.DANG_TIM && c.getBatDauTimLuc().isAfter(moc)).toList();
    }

    @Override
    public List<CuocCuuHo> timDangTimBatDauTruoc(LocalDateTime moc) {
        return duLieu.values().stream()
                .filter(c -> c.getTrangThai() == TrangThaiCuoc.DANG_TIM && !c.getBatDauTimLuc().isAfter(moc)).toList();
    }

    @Override
    public Optional<CuocCuuHo> timCuocDangLamCuaTho(Integer thoId) {
        return duLieu.values().stream()
                .filter(c -> thoId.equals(c.getThoId()) && c.dangHoatDong() && c.getTrangThai() != TrangThaiCuoc.DANG_TIM)
                .max(Comparator.comparing(CuocCuuHo::getId));
    }

    @Override
    public boolean nhanCuoc(Long cuocId, Integer thoId) {
        return capNhatCoDieuKien(cuocId, TrangThaiCuoc.DANG_TIM,
                c -> sao(c.getId(), c, thoId, TrangThaiCuoc.DA_NHAN, c.getTongTien(), c.getLyDoHuy(), c.getBatDauTimLuc()));
    }

    @Override
    public boolean thoTraCuoc(Long cuocId, Integer thoId, TrangThaiCuoc tu, LocalDateTime batDauTimLaiLuc) {
        CuocCuuHo hienTai = duLieu.get(cuocId);
        if (hienTai == null || !thoId.equals(hienTai.getThoId())) {
            return false;
        }
        return capNhatCoDieuKien(cuocId, tu,
                c -> sao(c.getId(), c, null, TrangThaiCuoc.DANG_TIM, c.getTongTien(), c.getLyDoHuy(), batDauTimLaiLuc));
    }

    @Override
    public boolean chuyenTrangThai(Long cuocId, TrangThaiCuoc tu, TrangThaiCuoc den) {
        return capNhatCoDieuKien(cuocId, tu,
                c -> sao(c.getId(), c, c.getThoId(), den, c.getTongTien(), c.getLyDoHuy(), c.getBatDauTimLuc()));
    }

    @Override
    public boolean chuyenSangChoThanhToan(Long cuocId, BigDecimal tongTien) {
        return capNhatCoDieuKien(cuocId, TrangThaiCuoc.DANG_SUA,
                c -> sao(c.getId(), c, c.getThoId(), TrangThaiCuoc.CHO_THANH_TOAN, tongTien, c.getLyDoHuy(), c.getBatDauTimLuc()));
    }

    @Override
    public boolean huyCuoc(Long cuocId, TrangThaiCuoc tu, String lyDo) {
        return capNhatCoDieuKien(cuocId, tu,
                c -> sao(c.getId(), c, c.getThoId(), TrangThaiCuoc.HUY, c.getTongTien(), lyDo, c.getBatDauTimLuc()));
    }

    /** Compare-and-set: chi thanh cong khi cuoc con o dung trang thai {@code mong} luc thay doi. */
    private boolean capNhatCoDieuKien(Long id, TrangThaiCuoc mong, UnaryOperator<CuocCuuHo> doi) {
        while (true) {
            CuocCuuHo hienTai = duLieu.get(id);
            if (hienTai == null || hienTai.getTrangThai() != mong) {
                return false;
            }
            if (duLieu.replace(id, hienTai, doi.apply(hienTai))) {
                return true;
            }
        }
    }

    private static CuocCuuHo sao(Long id, CuocCuuHo c, Integer thoId, TrangThaiCuoc trangThai,
                                 BigDecimal tongTien, String lyDo, LocalDateTime batDauTimLuc) {
        return CuocCuuHo.taiTao(id, c.getKhachId(), thoId, c.getLoaiSuCoId(), c.getViTri(), c.getDiaChi(),
                c.getGhiChu(), trangThai, c.getGiaUocTinh(), tongTien, lyDo, c.getNgayTao(), batDauTimLuc);
    }
}
