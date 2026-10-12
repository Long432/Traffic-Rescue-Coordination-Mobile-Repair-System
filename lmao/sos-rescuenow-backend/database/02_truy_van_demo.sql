\xEF\xBB\xBF/* ============================================================
   SOS RescueNow - Module 1: Truy van demo
   Chay SAU 01_tao_csdl.sql. Chay tung khoi (boi den roi F5).
   ============================================================ */
USE SosRescueNow;
GO

/* ---------- A. Tim tho gan (bounding box + khoang cach) ----------
   Backend thuc te: loc bounding box bang SQL, tinh Haversine o Java.
   O day dung them STDistance() de doi chieu (diem cong). */
DECLARE @lat DECIMAL(9,6) = 10.776900,
        @lng DECIMAL(9,6) = 106.700900,
        @banKinhKm FLOAT  = 5.0;

DECLARE @dLat FLOAT = @banKinhKm / 111.0;
DECLARE @dLng FLOAT = @banKinhKm / (111.0 * COS(RADIANS(CAST(@lat AS FLOAT))));

SELECT  t.Id AS ThoId, nd.HoTen, v.Latitude, v.Longitude,
        t.DiemTrungBinh, t.TiLeNhanCuoc,
        CAST(geography::Point(CAST(v.Latitude AS FLOAT), CAST(v.Longitude AS FLOAT), 4326)
             .STDistance(geography::Point(CAST(@lat AS FLOAT), CAST(@lng AS FLOAT), 4326)) / 1000.0
             AS DECIMAL(6,2)) AS KhoangCachKm
FROM Tho t
JOIN NguoiDung nd ON nd.Id = t.Id
JOIN ViTriTho  v  ON v.ThoId = t.Id
WHERE t.TrangThaiOnline = 1
  AND t.IsBusy = 0
  AND t.DaDuyet = 1
  AND v.Latitude  BETWEEN @lat - @dLat AND @lat + @dLat
  AND v.Longitude BETWEEN @lng - @dLng AND @lng + @dLng
ORDER BY KhoangCachKm;
-- Ky vong: tho 5 va 6 xuat hien; tho 7 (ban), tho 8 (xa), tho 9 (offline) khong xuat hien.
GO

/* ---------- B. Nhan cuoc an toan (atomic conditional update) ---------- */
INSERT INTO CuocCuuHo (KhachId, LoaiSuCoId, Latitude, Longitude, DiaChi, TrangThai, GiaUocTinh)
VALUES (2, 1, 10.776900, 106.700900, N'Địa chỉ mẫu', 'DANG_TIM', 80000);

DECLARE @cuocId BIGINT = SCOPE_IDENTITY();

-- Tho 5 bam "Nhan" truoc
UPDATE CuocCuuHo SET ThoId = 5, TrangThai = 'DA_NHAN'
WHERE Id = @cuocId AND TrangThai = 'DANG_TIM';
SELECT @@ROWCOUNT AS Tho5_NhanDuoc;      -- ky vong: 1

-- Tho 6 bam "Nhan" sau
UPDATE CuocCuuHo SET ThoId = 6, TrangThai = 'DA_NHAN'
WHERE Id = @cuocId AND TrangThai = 'DANG_TIM';
SELECT @@ROWCOUNT AS Tho6_NhanDuoc;      -- ky vong: 0 (da co nguoi nhan)

SELECT Id, ThoId, TrangThai FROM CuocCuuHo WHERE Id = @cuocId;  -- ThoId phai la 5
GO

/* ---------- C. Doi chieu: cach dung UPDLOCK (Pessimistic) ---------- */
DECLARE @cuoc2 BIGINT;
INSERT INTO CuocCuuHo (KhachId, LoaiSuCoId, Latitude, Longitude, TrangThai, GiaUocTinh)
VALUES (3, 2, 10.776900, 106.700900, 'DANG_TIM', 60000);
SET @cuoc2 = SCOPE_IDENTITY();

BEGIN TRAN;
    DECLARE @tt VARCHAR(20);
    SELECT @tt = TrangThai FROM CuocCuuHo WITH (UPDLOCK, ROWLOCK) WHERE Id = @cuoc2;
    IF @tt = 'DANG_TIM'
        UPDATE CuocCuuHo SET ThoId = 6, TrangThai = 'DA_NHAN' WHERE Id = @cuoc2;
COMMIT TRAN;

SELECT Id, ThoId, TrangThai FROM CuocCuuHo WHERE Id = @cuoc2;   -- ThoId = 6
GO
