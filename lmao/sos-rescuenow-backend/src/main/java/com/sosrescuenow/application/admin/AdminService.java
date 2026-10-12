package com.sosrescuenow.application.admin;

import com.sosrescuenow.application.port.HoSoThoRepository;
import com.sosrescuenow.application.port.LoaiSuCoRepository;
import com.sosrescuenow.domain.exception.DuLieuKhongHopLeException;
import com.sosrescuenow.domain.exception.KhongTimThayException;
import com.sosrescuenow.domain.model.LoaiSuCo;
import com.sosrescuenow.domain.model.ThoTomTat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Use case cua quan tri vien: xem va duyet ho so tho. */
@Service
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final HoSoThoRepository hoSoThoRepository;
    private final LoaiSuCoRepository loaiSuCoRepository;

    public AdminService(HoSoThoRepository hoSoThoRepository, LoaiSuCoRepository loaiSuCoRepository) {
        this.hoSoThoRepository = hoSoThoRepository;
        this.loaiSuCoRepository = loaiSuCoRepository;
    }

    @Transactional(readOnly = true)
    public List<ThoTomTat> danhSachTho(boolean chiChuaDuyet) {
        return hoSoThoRepository.lietKe(chiChuaDuyet);
    }

    /**
     * Duyet tho. {@code loaiSuCoIds} rong hoac null nghia la cho phep lam TAT CA loai su co.
     * Duyet lai tho da duoc duyet thi chi cap nhat danh sach loai su co (khong gay loi).
     */
    @Transactional
    public void duyetTho(Integer thoId, Set<Integer> loaiSuCoIds) {
        hoSoThoRepository.timHoSo(thoId).orElseThrow(() -> new KhongTimThayException("Không tìm thấy hồ sơ thợ"));

        Set<Integer> duocChon;
        if (loaiSuCoIds == null || loaiSuCoIds.isEmpty()) {
            duocChon = loaiSuCoRepository.timTatCa().stream().map(LoaiSuCo::id).collect(Collectors.toSet());
        } else {
            duocChon = new HashSet<>(loaiSuCoIds);
            for (Integer id : duocChon) {
                if (loaiSuCoRepository.timTheoId(id).isEmpty()) {
                    throw new DuLieuKhongHopLeException("Loại sự cố không tồn tại: " + id);
                }
            }
        }
        hoSoThoRepository.duyetTho(thoId, duocChon);
        log.info("Admin duyet tho {} voi {} loai su co", thoId, duocChon.size());
    }
}
