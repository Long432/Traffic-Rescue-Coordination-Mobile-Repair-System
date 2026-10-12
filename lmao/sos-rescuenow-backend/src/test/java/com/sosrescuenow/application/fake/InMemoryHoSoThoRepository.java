package com.sosrescuenow.application.fake;

import com.sosrescuenow.application.port.HoSoThoRepository;
import com.sosrescuenow.domain.dieuphoi.UngVienTho;
import com.sosrescuenow.domain.model.HoSoTho;
import com.sosrescuenow.domain.model.ThoTomTat;
import com.sosrescuenow.domain.model.ViTri;
import com.sosrescuenow.domain.model.ViTriTho;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

/** Ban gia trong bo nho. Cac ham deu synchronized de gia lap tinh nguyen tu cua CSDL. */
public class InMemoryHoSoThoRepository implements HoSoThoRepository {

    private static final class TrangThai {
        boolean online;
        boolean dangBan;
        boolean daDuyet;
        double sao;
        double tiLe;
        final Set<Integer> loai = new HashSet<>();
    }

    private final Map<Integer, TrangThai> thos = new TreeMap<>();
    private final InMemoryViTriThoRepository viTriRepo;

    public InMemoryHoSoThoRepository(InMemoryViTriThoRepository viTriRepo) {
        this.viTriRepo = viTriRepo;
    }

    public synchronized void them(int id, boolean online, boolean dangBan, boolean daDuyet,
                                  double sao, double tiLe, Integer... loaiSuCo) {
        TrangThai t = new TrangThai();
        t.online = online;
        t.dangBan = dangBan;
        t.daDuyet = daDuyet;
        t.sao = sao;
        t.tiLe = tiLe;
        t.loai.addAll(List.of(loaiSuCo));
        thos.put(id, t);
    }

    @Override
    public synchronized void taoHoSoMoi(Integer id) {
        them(id, false, false, false, 5.0, 1.0);
    }

    @Override
    public synchronized Optional<HoSoTho> timHoSo(Integer thoId) {
        TrangThai t = thos.get(thoId);
        return t == null ? Optional.empty()
                : Optional.of(new HoSoTho(thoId, t.online, t.dangBan, t.daDuyet, t.sao, t.tiLe, t.loai));
    }

    @Override
    public synchronized void datTrangThaiOnline(Integer thoId, boolean online) {
        thos.get(thoId).online = online;
    }

    @Override
    public synchronized boolean datBanNeuRanh(Integer thoId) {
        TrangThai t = thos.get(thoId);
        if (t == null || !t.daDuyet || !t.online || t.dangBan) {
            return false;
        }
        t.dangBan = true;
        return true;
    }

    @Override
    public synchronized void datRanh(Integer thoId) {
        thos.get(thoId).dangBan = false;
    }

    @Override
    public synchronized void datDiemTrungBinh(Integer thoId, double diem) {
        thos.get(thoId).sao = diem;
    }

    @Override
    public synchronized List<ThoTomTat> lietKe(boolean chiChuaDuyet) {
        List<ThoTomTat> kq = new ArrayList<>();
        for (Map.Entry<Integer, TrangThai> e : thos.entrySet()) {
            TrangThai t = e.getValue();
            if (!chiChuaDuyet || !t.daDuyet) {
                kq.add(new ThoTomTat(e.getKey(), "Thợ " + e.getKey(), String.format("09%08d", e.getKey()),
                        t.daDuyet, t.online, t.dangBan, t.sao));
            }
        }
        java.util.Collections.reverse(kq); // moi nhat (id lon) truoc, giong ban that
        return kq;
    }

    @Override
    public synchronized void duyetTho(Integer thoId, Set<Integer> loaiSuCoIds) {
        TrangThai t = thos.get(thoId);
        t.daDuyet = true;
        t.loai.clear();
        t.loai.addAll(loaiSuCoIds);
    }

    @Override
    public synchronized List<UngVienTho> timUngVienRanh(ViTri tam, double banKinhKm, Integer loaiSuCoId,
                                                        LocalDateTime viTriMoiTu) {
        List<UngVienTho> ketQua = new ArrayList<>();
        for (Map.Entry<Integer, TrangThai> e : thos.entrySet()) {
            TrangThai t = e.getValue();
            if (!t.daDuyet || !t.online || t.dangBan || !t.loai.contains(loaiSuCoId)) {
                continue;
            }
            Optional<ViTriTho> vt = viTriRepo.timTheoThoId(e.getKey());
            if (vt.isEmpty() || vt.get().capNhatLuc().isBefore(viTriMoiTu)) {
                continue;
            }
            if (tam.khoangCachKm(vt.get().viTri()) <= banKinhKm) {
                ketQua.add(new UngVienTho(e.getKey(), vt.get().viTri(), t.sao, t.tiLe));
            }
        }
        return ketQua;
    }
}
