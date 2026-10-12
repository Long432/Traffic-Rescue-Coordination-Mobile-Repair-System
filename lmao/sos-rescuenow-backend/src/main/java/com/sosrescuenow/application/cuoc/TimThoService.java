package com.sosrescuenow.application.cuoc;

import com.sosrescuenow.application.port.HoSoThoRepository;
import com.sosrescuenow.domain.dieuphoi.ChienLuocChamDiem;
import com.sosrescuenow.domain.dieuphoi.QuyTacPhatCuoc;
import com.sosrescuenow.domain.dieuphoi.UngVienDaXepHang;
import com.sosrescuenow.domain.dieuphoi.UngVienTho;
import com.sosrescuenow.domain.model.ViTri;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/** Tim va xep hang tho phu hop cho mot diem: loc o CSDL (bounding box), tinh chinh xac va cham diem o Java. */
@Service
public class TimThoService {

    private final HoSoThoRepository hoSoThoRepository;
    private final ChienLuocChamDiem chienLuocChamDiem;

    public TimThoService(HoSoThoRepository hoSoThoRepository, ChienLuocChamDiem chienLuocChamDiem) {
        this.hoSoThoRepository = hoSoThoRepository;
        this.chienLuocChamDiem = chienLuocChamDiem;
    }

    /** @return tho trong ban kinh, diem cao nhat truoc; bang diem thi gan hon truoc, roi theo id (on dinh) */
    public List<UngVienDaXepHang> xepHang(ViTri tam, Integer loaiSuCoId, double banKinhKm, LocalDateTime bayGio) {
        LocalDateTime viTriMoiTu = bayGio.minus(QuyTacPhatCuoc.THOI_GIAN_VI_TRI_HOP_LE);
        List<UngVienTho> ungViens = hoSoThoRepository.timUngVienRanh(tam, banKinhKm, loaiSuCoId, viTriMoiTu);

        return ungViens.stream()
                .map(u -> new KhoangCach(u, tam.khoangCachKm(u.viTri())))
                .filter(k -> k.km() <= banKinhKm)
                .map(k -> new UngVienDaXepHang(k.ungVien(), k.km(),
                        chienLuocChamDiem.cham(k.ungVien(), k.km(), banKinhKm)))
                .sorted(Comparator.comparingDouble(UngVienDaXepHang::diem).reversed()
                        .thenComparingDouble(UngVienDaXepHang::khoangCachKm)
                        .thenComparing(x -> x.ungVien().thoId()))
                .toList();
    }

    private record KhoangCach(UngVienTho ungVien, double km) {
    }
}
