/* ============================================================
   SOS RescueNow - Duyet tho de thu nghiem (PostgreSQL)
   Chay trong psql (PostgreSQL). Sua @thoId thanh Id cua tai khoan tho vua dang ky.
   ============================================================ */

/* Postgre khong co USE, su dung \c hoac dinh don db tren command line*/
-- \c SosRescueNow;

//CHƯA TEST CHẠY LỆNH

DECLARE @thoId INT = 0;   -- <== DOI SO NAY (xem Id trong ket qua dang ky, hoac SELECT ben duoi)

-- Danh sach tho hien co:
SELECT t.Id, nd.HoTen, nd.SoDienThoai, t.DaDuyet, t.TrangThaiOnline, t.IsBusy
FROM Tho t JOIN NguoiDung nd ON nd.Id = t.Id ORDER BY t.Id DESC;

IF @thoId > 0
BEGIN
    UPDATE Tho SET DaDuyet = 1 WHERE Id = @thoId;

    -- Cho tho lam duoc TAT CA loai su co (chua co thi them)
    INSERT INTO ThoLoaiSuCo (ThoId, LoaiSuCoId)
    SELECT @thoId, l.Id FROM LoaiSuCo l
    WHERE NOT EXISTS (SELECT 1 FROM ThoLoaiSuCo x WHERE x.ThoId = @thoId AND x.LoaiSuCoId = l.Id);

    SELECT N'Da duyet tho ' + CAST(@thoId AS NVARCHAR(10)) AS KetQua;
END
GO