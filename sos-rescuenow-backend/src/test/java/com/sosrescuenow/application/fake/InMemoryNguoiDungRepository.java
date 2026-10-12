package com.sosrescuenow.application.fake;

import com.sosrescuenow.application.port.NguoiDungRepository;
import com.sosrescuenow.domain.model.NguoiDung;
import com.sosrescuenow.domain.model.TrangThaiTaiKhoan;
import com.sosrescuenow.domain.model.VaiTro;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryNguoiDungRepository implements NguoiDungRepository {

    private final Map<Integer, NguoiDung> duLieu = new ConcurrentHashMap<>();
    private final AtomicInteger idKeTiep = new AtomicInteger(1);

    /** Them nhanh mot nguoi dung voi id cho truoc (dung cho test). */
    public void them(int id, String hoTen, VaiTro vaiTro) {
        duLieu.put(id, NguoiDung.taiTao(id, hoTen, String.format("09%08d", id), "HASH", vaiTro,
                TrangThaiTaiKhoan.HOAT_DONG, null));
    }

    @Override
    public Optional<NguoiDung> timTheoId(Integer id) {
        return Optional.ofNullable(duLieu.get(id));
    }

    @Override
    public Optional<NguoiDung> timTheoSoDienThoai(String soDienThoai) {
        return duLieu.values().stream().filter(n -> n.getSoDienThoai().equals(soDienThoai)).findFirst();
    }

    @Override
    public boolean tonTaiSoDienThoai(String soDienThoai) {
        return timTheoSoDienThoai(soDienThoai).isPresent();
    }

    @Override
    public NguoiDung luu(NguoiDung nd) {
        int id = idKeTiep.getAndIncrement();
        NguoiDung daLuu = NguoiDung.taiTao(id, nd.getHoTen(), nd.getSoDienThoai(), nd.getMatKhauHash(),
                nd.getVaiTro(), nd.getTrangThaiTaiKhoan(), nd.getKhoaDen());
        duLieu.put(id, daLuu);
        return daLuu;
    }
}
