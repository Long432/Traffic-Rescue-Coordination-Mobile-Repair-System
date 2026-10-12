\xEF\xBB\xBF/* ============================================================
   SOS RescueNow - Module 3: NANG CAP CSDL DANG CO (KHONG MAT DU LIEU)
   Chay MOT LAN trong SSMS, dang nhap bang Windows Authentication (can quyen ALTER).
   An toan khi chay lai: chi them cot neu chua co.
   Khong dung 01_tao_csdl.sql cho viec nay, vi 01 se XOA het du lieu.
   ============================================================ */
USE SosRescueNow;
GO

IF COL_LENGTH('dbo.CuocCuuHo', 'BatDauTimLuc') IS NULL
    ALTER TABLE dbo.CuocCuuHo
        ADD BatDauTimLuc DATETIME2 NOT NULL
            CONSTRAINT DF_Cuoc_BatDauTim DEFAULT SYSUTCDATETIME();
GO

IF COL_LENGTH('dbo.LichSuTrangThai', 'GhiChu') IS NULL
    ALTER TABLE dbo.LichSuTrangThai ADD GhiChu NVARCHAR(255) NULL;
GO

SELECT
    CASE WHEN COL_LENGTH('dbo.CuocCuuHo', 'BatDauTimLuc') IS NOT NULL THEN N'OK' ELSE N'THIEU' END AS CuocCuuHo_BatDauTimLuc,
    CASE WHEN COL_LENGTH('dbo.LichSuTrangThai', 'GhiChu') IS NOT NULL THEN N'OK' ELSE N'THIEU' END AS LichSu_GhiChu;
GO
