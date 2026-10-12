package com.sosrescuenow.domain;

import com.sosrescuenow.domain.model.TrangThaiCuoc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class TrangThaiCuocTest {

    @ParameterizedTest(name = "{0} -> {1} hop le")
    @CsvSource({
            "DANG_TIM, DA_NHAN",
            "DANG_TIM, KHONG_CO_THO",
            "DANG_TIM, HUY",
            "DA_NHAN, DANG_DEN",
            "DA_NHAN, DANG_TIM",
            "DA_NHAN, HUY",
            "DANG_DEN, DA_DEN",
            "DANG_DEN, DANG_TIM",
            "DANG_DEN, HUY",
            "DA_DEN, DANG_SUA",
            "DA_DEN, HUY",
            "DANG_SUA, CHO_THANH_TOAN",
            "CHO_THANH_TOAN, HOAN_THANH"
    })
    void chuyenHopLe(TrangThaiCuoc tu, TrangThaiCuoc den) {
        assertThat(tu.coTheChuyenSang(den)).isTrue();
    }

    @ParameterizedTest(name = "{0} -> {1} khong hop le")
    @CsvSource({
            "DANG_TIM, HOAN_THANH",
            "DANG_TIM, DANG_SUA",
            "DA_NHAN, DANG_SUA",
            "DANG_DEN, DANG_SUA",
            "DANG_SUA, HUY",
            "CHO_THANH_TOAN, HUY",
            "HOAN_THANH, DANG_TIM",
            "HUY, DANG_TIM",
            "KHONG_CO_THO, DA_NHAN"
    })
    void chuyenKhongHopLe(TrangThaiCuoc tu, TrangThaiCuoc den) {
        assertThat(tu.coTheChuyenSang(den)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = TrangThaiCuoc.class, names = {"HOAN_THANH", "HUY", "KHONG_CO_THO"})
    void trangThaiKetThucKhongCoDuongDi(TrangThaiCuoc trangThai) {
        assertThat(trangThai.laKetThuc()).isTrue();
        assertThat(trangThai.cacTrangThaiTiepTheo()).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = TrangThaiCuoc.class, names = {"DANG_TIM", "DA_NHAN", "DANG_DEN", "DA_DEN", "DANG_SUA", "CHO_THANH_TOAN"})
    void trangThaiDangXuLyKhongPhaiKetThuc(TrangThaiCuoc trangThai) {
        assertThat(trangThai.laKetThuc()).isFalse();
    }

    @Test
    void khongTrangThaiNaoChuyenSangChinhNo() {
        for (TrangThaiCuoc trangThai : TrangThaiCuoc.values()) {
            assertThat(trangThai.coTheChuyenSang(trangThai)).as(trangThai.name()).isFalse();
        }
    }
}
