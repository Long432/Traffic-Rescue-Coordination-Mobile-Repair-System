package com.sosrescuenow.integration;

import com.sosrescuenow.application.cuoc.ThoCuocService;
import com.sosrescuenow.domain.exception.XungDotException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Kiem chung tren SQL SERVER THAT: 10 tho cung bam "Nhan" mot cuoc, chi mot tho thanh cong.
 * Mac dinh BI BO QUA. Chay bang:  $env:SOS_IT = '1'; mvn test -Dtest=NhanCuocDongThoiIT
 * (can du ba bien moi truong DB_USER, DB_PASSWORD, JWT_SECRET). Test tu tao va tu xoa du lieu cua no.
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "SOS_IT", matches = "1")
class NhanCuocDongThoiIT {

    private static final int SO_THO = 10;

    @Autowired
    private ThoCuocService thoCuocService;

    @Autowired
    private JdbcTemplate jdbc;

    private int taoNguoiDung(String vaiTro) {
        String sdt = String.format("08%08d", ThreadLocalRandom.current().nextInt(100_000_000));
        jdbc.update("INSERT INTO NguoiDung (HoTen, SoDienThoai, MatKhauHash, VaiTro) VALUES (?, ?, 'IT_KHONG_DUNG', ?)",
                "IT " + vaiTro, sdt, vaiTro);
        return jdbc.queryForObject("SELECT Id FROM NguoiDung WHERE SoDienThoai = ?", Integer.class, sdt);
    }

    @Test
    void muoiThoCungNhan_chiMotThoThanhCongTrenSqlServerThat() throws Exception {
        List<Integer> thoIds = new ArrayList<>();
        int khachId = taoNguoiDung("USER");
        Long cuocId = null;
        try {
            for (int i = 0; i < SO_THO; i++) {
                int thoId = taoNguoiDung("THO");
                thoIds.add(thoId);
                jdbc.update("INSERT INTO Tho (Id, TrangThaiOnline, IsBusy, DaDuyet) VALUES (?, 1, 0, 1)", thoId);
                jdbc.update("INSERT INTO ThoLoaiSuCo (ThoId, LoaiSuCoId) VALUES (?, 1)", thoId);
                jdbc.update("INSERT INTO ViTriTho (ThoId, Latitude, Longitude, CapNhatLuc) "
                        + "VALUES (?, 10.777000, 106.701000, SYSUTCDATETIME())", thoId);
            }
            jdbc.update("INSERT INTO CuocCuuHo (KhachId, LoaiSuCoId, Latitude, Longitude, TrangThai, GiaUocTinh, BatDauTimLuc) "
                    + "VALUES (?, 1, 10.776900, 106.700900, 'DANG_TIM', 80000, DATEADD(second, -30, SYSUTCDATETIME()))", khachId);
            cuocId = jdbc.queryForObject("SELECT MAX(Id) FROM CuocCuuHo WHERE KhachId = ?", Long.class, khachId);
            final Long cuocCanNhan = cuocId;

            ExecutorService pool = Executors.newFixedThreadPool(SO_THO);
            CountDownLatch batDau = new CountDownLatch(1);
            List<Future<Integer>> ketQua = new ArrayList<>();
            for (Integer thoId : thoIds) {
                Callable<Integer> viec = () -> {
                    batDau.await();
                    try {
                        thoCuocService.nhanCuoc(thoId, cuocCanNhan);
                        return thoId;
                    } catch (XungDotException e) {
                        return -1;
                    }
                };
                ketQua.add(pool.submit(viec));
            }
            batDau.countDown();
            List<Integer> thanhCong = new ArrayList<>();
            for (Future<Integer> f : ketQua) {
                Integer v = f.get(30, TimeUnit.SECONDS);
                if (v > 0) {
                    thanhCong.add(v);
                }
            }
            pool.shutdown();

            assertThat(thanhCong).hasSize(1);
            Integer thoThang = jdbc.queryForObject("SELECT ThoId FROM CuocCuuHo WHERE Id = ?", Integer.class, cuocCanNhan);
            assertThat(thoThang).isEqualTo(thanhCong.get(0));
            Integer soThoBan = jdbc.queryForObject("SELECT COUNT(*) FROM Tho WHERE Id IN ("
                    + String.join(",", thoIds.stream().map(String::valueOf).toList()) + ") AND IsBusy = 1", Integer.class);
            assertThat(soThoBan).isEqualTo(1);
            Integer soDongLichSu = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM LichSuTrangThai WHERE CuocId = ? AND DenTrangThai = 'DA_NHAN'", Integer.class, cuocCanNhan);
            assertThat(soDongLichSu).isEqualTo(1);
        } finally {
            if (cuocId != null) {
                jdbc.update("DELETE FROM LichSuTrangThai WHERE CuocId = ?", cuocId);
                jdbc.update("DELETE FROM CuocCuuHo WHERE Id = ?", cuocId);
            }
            for (Integer thoId : thoIds) {
                jdbc.update("DELETE FROM ViTriTho WHERE ThoId = ?", thoId);
                jdbc.update("DELETE FROM ThoLoaiSuCo WHERE ThoId = ?", thoId);
                jdbc.update("DELETE FROM Tho WHERE Id = ?", thoId);
                jdbc.update("DELETE FROM NguoiDung WHERE Id = ?", thoId);
            }
            jdbc.update("DELETE FROM NguoiDung WHERE Id = ?", khachId);
        }
    }
}
