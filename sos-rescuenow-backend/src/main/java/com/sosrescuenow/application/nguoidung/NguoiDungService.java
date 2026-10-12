package com.sosrescuenow.application.nguoidung;

import com.sosrescuenow.application.port.NguoiDungRepository;
import com.sosrescuenow.domain.exception.KhongTimThayException;
import com.sosrescuenow.domain.model.NguoiDung;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;

    public NguoiDungService(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Transactional(readOnly = true)
    public ThongTinNguoiDung layThongTin(Integer nguoiDungId) {
        NguoiDung nd = nguoiDungRepository.timTheoId(nguoiDungId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy người dùng"));
        return new ThongTinNguoiDung(nd.getId(), nd.getHoTen(), nd.getSoDienThoai(), nd.getVaiTro());
    }
}
