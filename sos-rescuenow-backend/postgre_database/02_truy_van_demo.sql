/* ============================================================
   SOS RescueNow - Module 1: Truy van demo (PostgreSQL)
   Chay sau 01_tao_csdl.sql. Chay tung khoi (boi den roi F5).
   ============================================================ */

/* ---------- A. Tim tho gan (bounding box + khoang cach) ----------
   PostgreSQL su dung ST_Distance trog PostGIS hoac tinh toan tren toa do.
   O day su dung conginh Haversine thuần tuan de khong depend PostGIS. */

//CHƯA TEST CHẠY LỆNH

SELECT  t.Id AS ThoId, nd.HoTen, v.Latitude, v.Longitude,
        t.DiemTrungBinh, t.TiLeNhanCuoc,
        -- Tinh khoang cach Haversine (km)
        ROUND(
          6371.0 * ACOS(
            COS(RADIANS(CAST(v.Latitude AS FLOAT))) * COS(RADIANS(CAST(@lat AS FLOAT)))
            * COS(RADIANS(CAST(@lng AS FLOAT)) - RADIANS(CAST(v.Longitude AS FLOAT)))
            + SIN(RADIANS(CAST(v.Latitude AS FLOAT))) * SIN(RADIANS(CAST(@lat AS FLOAT)))
          )
        ) AS KhoangCachKm
FROM Tho t
JOIN NguoiDung nd ON nd.Id = t.Id
JOIN ViTriTho  v  ON v.ThoId = t.Id
WHERE t.TrangThaiOnline = TRUE
  AND t.IsBusy = FALSE
  AND t.DaDuyet = TRUE
  AND v.Latitude  BETWEEN @lat - @dLat AND @lat + @dLat
  AND v.Longitude BETWEEN @lng - @dLng AND @lng + @dLng
ORDER BY KhoangCachKm;
/-- Ky vong: tho 5 va 6 xuat hien; tho 7 (ban), tho 8 (xa), tho 9 (offline) khong xuat hien.

/* ---------- B. Nhan cuoc an toan (atomic conditional update) ---------- */
INSERT INTO CuocCuuHo (KhachId, LoaiSuCoId, Latitude, Longitude, DiaChi, TrangThai, GiaUocTinh)
VALUES (2, 1, 10.776900, 106.700900, 'Dia chi mau', 'DANG_TIM', 80000);

DECLARE @cuocId BIGINT = CAST(SCOPE_IDENTITY() AS BIGINT); -- Postgre khong co SCOPE_IDENTITY, su dung RETURNING

-- Tho 5 bam "Nhan" truoc
UPDATE CuocCuuHo SET ThoId = 5, TrangThai = 'DA_NHAN'
WHERE Id = @cuocId AND TrangThai = 'DANG_TIM';
-- Postgre su dung RETURNING thay cho SELECT @@ROWCOUNT
SELECT COUNT(*) AS Tho5_NhanDuoc FROM CuocCuuHo WHERE Id = @cuocId AND ThoId = 5 AND TrangThai = 'DA_NHAN';

-- Tho 6 bam "Nhan" sau
UPDATE CuocCuuHo SET ThoId = 6, TrangThai = 'DA_NHAN'
WHERE Id = @cuocId AND TrangThai = 'DANG_TIM';
SELECT COUNT(*) AS Tho6_NhanDuoc FROM CuocCuuHo WHERE Id = @cuocId AND ThoId = 6 AND TrangThai = 'DA_NHAN';

SELECT Id, ThoId, TrangThai FROM CuocCuuHo WHERE Id = @cuocId;
/-- ThoId phai la 5

/* ---------- C. Doi chieu: cach dung UPDLOCK (Pessimistic) ---------- */
DECLARE @cuoc2 BIGINT;
INSERT INTO CuocCuuHo (KhachId, LoaiSuCoId, Latitude, Longitude, TrangThai, GiaUocTinh)
VALUES (3, 2, 10.776900, 106.700900, 'DANG_TIM', 60000);
SET @cuoc2 = CAST(SCOPE_IDENTITY() AS BIGINT);

BEGIN TRANSACTION;
    DECLARE @tt VARCHAR(20);
    SELECT @tt = TrangThai FROM CuocCuuHo WHERE Id = @cuoc2 FOR UPDATE;
    IF @tt = 'DANG_TIM'
        UPDATE CuocCuuHo SET ThoId = 6, TrangThai = 'DA_NHAN' WHERE Id = @cuoc2;
COMMIT;

SELECT Id, ThoId, TrangThai FROM CuocCuuHo WHERE Id = @cuoc2;
/-- ThoId = 6