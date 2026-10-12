package com.sosrescuenow.infrastructure.persistence.adapter;

import com.sosrescuenow.application.port.NguoiDungRepository;
import com.sosrescuenow.domain.exception.TrungLapException;
import com.sosrescuenow.domain.model.NguoiDung;
import com.sosrescuenow.infrastructure.persistence.entity.NguoiDungEntity;
import com.sosrescuenow.infrastructure.persistence.repository.NguoiDungJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Cai dat cong NguoiDungRepository bang Spring Data JPA, kem chuyen doi entity <-> domain. */
@Repository
class NguoiDungRepositoryAdapter implements NguoiDungRepository {

    /** Ten rang buoc UNIQUE tren cot SoDienThoai (xem 01_tao_csdl.sql). */
    private static final String RANG_BUOC_SDT = "UQ_NguoiDung_SDT";

    private final NguoiDungJpaRepository jpa;

    NguoiDungRepositoryAdapter(NguoiDungJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<NguoiDung> timTheoId(Integer id) {
        return jpa.findById(id).map(NguoiDungRepositoryAdapter::sangDomain);
    }

    @Override
    public Optional<NguoiDung> timTheoSoDienThoai(String soDienThoai) {
        return jpa.findBySoDienThoai(soDienThoai).map(NguoiDungRepositoryAdapter::sangDomain);
    }

    @Override
    public boolean tonTaiSoDienThoai(String soDienThoai) {
        return jpa.existsBySoDienThoai(soDienThoai);
    }

    @Override
    public NguoiDung luu(NguoiDung nguoiDung) {
        try {
            return sangDomain(jpa.save(sangEntity(nguoiDung)));
        } catch (DataIntegrityViolationException e) {
            // Hai yeu cau dang ky cung so dien thoai cung luc: kiem tra "ton tai" ben tren co the
            // cung qua, nen rang buoc UNIQUE cua CSDL la hang rao cuoi cung.
            if (laViPhamSoDienThoaiTrung(e)) {
                throw new TrungLapException("Số điện thoại đã được đăng ký");
            }
            throw e; // vi pham khac: khong che giau
        }
    }

    private static boolean laViPhamSoDienThoaiTrung(DataIntegrityViolationException e) {
        String thongBao = e.getMostSpecificCause().getMessage();
        return thongBao != null && thongBao.contains(RANG_BUOC_SDT);
    }

    private static NguoiDung sangDomain(NguoiDungEntity e) {
        return NguoiDung.taiTao(e.getId(), e.getHoTen(), e.getSoDienThoai(), e.getMatKhauHash(),
                e.getVaiTro(), e.getTrangThaiTaiKhoan(), e.getKhoaDen());
    }

    private static NguoiDungEntity sangEntity(NguoiDung nd) {
        NguoiDungEntity e = new NguoiDungEntity();
        e.setId(nd.getId());
        e.setHoTen(nd.getHoTen());
        e.setSoDienThoai(nd.getSoDienThoai());
        e.setMatKhauHash(nd.getMatKhauHash());
        e.setVaiTro(nd.getVaiTro());
        e.setTrangThaiTaiKhoan(nd.getTrangThaiTaiKhoan());
        e.setKhoaDen(nd.getKhoaDen());
        return e;
    }
}
