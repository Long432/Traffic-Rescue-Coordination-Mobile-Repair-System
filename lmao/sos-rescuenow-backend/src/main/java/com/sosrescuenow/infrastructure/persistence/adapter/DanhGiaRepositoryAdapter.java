package com.sosrescuenow.infrastructure.persistence.adapter;

import com.sosrescuenow.application.port.DanhGiaRepository;
import com.sosrescuenow.domain.exception.XungDotException;
import com.sosrescuenow.domain.model.DanhGia;
import com.sosrescuenow.infrastructure.persistence.entity.DanhGiaEntity;
import com.sosrescuenow.infrastructure.persistence.repository.DanhGiaJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class DanhGiaRepositoryAdapter implements DanhGiaRepository {

    /** Ten rang buoc UNIQUE tren cot CuocId (xem 01_tao_csdl.sql): moi cuoc chi duoc danh gia mot lan. */
    private static final String RANG_BUOC_CUOC = "UQ_DanhGia_Cuoc";

    private final DanhGiaJpaRepository jpa;

    DanhGiaRepositoryAdapter(DanhGiaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<DanhGia> timTheoCuoc(Long cuocId) {
        return jpa.findByCuocId(cuocId).map(e -> new DanhGia(e.getCuocId(), e.getSoSao(), e.getNhanXet()));
    }

    @Override
    public void luu(DanhGia danhGia) {
        DanhGiaEntity e = new DanhGiaEntity();
        e.setCuocId(danhGia.cuocId());
        e.setSoSao(danhGia.soSao());
        e.setNhanXet(danhGia.nhanXet());
        try {
            jpa.save(e);
        } catch (DataIntegrityViolationException ex) {
            // Hai yeu cau danh gia cung luc: kiem tra "da co" o service co the cung qua; CSDL la hang rao cuoi.
            String thongBao = ex.getMostSpecificCause().getMessage();
            if (thongBao != null && thongBao.contains(RANG_BUOC_CUOC)) {
                throw new XungDotException("Bạn đã đánh giá cuốc này rồi");
            }
            throw ex;
        }
    }

    @Override
    public List<Integer> soSaoCuaTho(Integer thoId) {
        return jpa.soSaoCuaTho(thoId);
    }
}
