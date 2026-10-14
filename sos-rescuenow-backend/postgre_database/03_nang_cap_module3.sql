/* ============================================================
   SOS RescueNow - Module 3: NANG CAP CSDL DANG CO (PostgreSQL)
   Chay MOT LAN trong psql, them cot neu chua co.
   An toan khi chay lai: chi them cot neu chua co.
   ============================================================ */

/* PostgreSQL su dung information_schema.columns de kiem tra existence */

//CHƯA TEST CHẠY LỆNH

SELECT
    CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'CuocCuuHo' AND column_name = 'BatDauTimLuc') THEN 'OK' ELSE 'THIEU' END AS CuocCuuHo_BatDauTimLuc,
    CASE WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'LichSuTrangThai' AND column_name = 'GhiChu') THEN 'OK' ELSE 'THIEU' END AS LichSu_GhiChu;

/* Them cot BatDauTimLuc neu chua co */
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'CuocCuuHo' AND column_name = 'BatDauTimLuc') THEN
        ALTER TABLE CuocCuuHo ADD COLUMN BatDauTimLuc TIMESTAMP NOT NULL DEFAULT NOW();
    END IF;
END
$$;

/* Them cot GhiChu neu chua co */
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'LichSuTrangThai' AND column_name = 'GhiChu') THEN
        ALTER TABLE LichSuTrangThai ADD COLUMN GhiChu VARCHAR(255);
    END IF;
END
$$=