\xEF\xBB\xBF/* ============================================================
   SOS RescueNow - Module 1: Tao co so du lieu (SQL Server)
   Chay trong SSMS: mo file -> Execute (F5)
   Script co the chay lai nhieu lan (se XOA va TAO LAI cac bang)
   ============================================================ */
IF DB_ID(N'SosRescueNow') IS NULL
    CREATE DATABASE SosRescueNow COLLATE Vietnamese_CI_AS;
GO
USE SosRescueNow;
GO

/* ---------- Xoa bang cu (theo thu tu khoa ngoai) ---------- */
DROP TABLE IF EXISTS DanhGia;
DROP TABLE IF EXISTS VatTuPhatSinh;
DROP TABLE IF EXISTS LichSuTrangThai;
DROP TABLE IF EXISTS CuocCuuHo;
DROP TABLE IF EXISTS ThoLoaiSuCo;
DROP TABLE IF EXISTS ViTriTho;
DROP TABLE IF EXISTS Tho;
DROP TABLE IF EXISTS LoaiSuCo;
DROP TABLE IF EXISTS NguoiDung;
GO

/* ---------- 1. NguoiDung ---------- */
CREATE TABLE NguoiDung (
    Id                 INT IDENTITY(1,1) CONSTRAINT PK_NguoiDung PRIMARY KEY,
    HoTen              NVARCHAR(100) NOT NULL,
    SoDienThoai        VARCHAR(15)   NOT NULL CONSTRAINT UQ_NguoiDung_SDT UNIQUE,
    MatKhauHash        VARCHAR(100)  NOT NULL,
    VaiTro             VARCHAR(10)   NOT NULL CONSTRAINT CK_NguoiDung_VaiTro CHECK (VaiTro IN ('USER','THO','ADMIN')),
    TrangThaiTaiKhoan  VARCHAR(10)   NOT NULL CONSTRAINT DF_NguoiDung_TT DEFAULT 'HOAT_DONG'
                       CONSTRAINT CK_NguoiDung_TT CHECK (TrangThaiTaiKhoan IN ('HOAT_DONG','BI_KHOA')),
    KhoaDen            DATETIME2     NULL,
    NgayTao            DATETIME2     NOT NULL CONSTRAINT DF_NguoiDung_NgayTao DEFAULT SYSUTCDATETIME()
);

/* ---------- 2. LoaiSuCo ---------- */
CREATE TABLE LoaiSuCo (
    Id          INT IDENTITY(1,1) CONSTRAINT PK_LoaiSuCo PRIMARY KEY,
    TenSuCo     NVARCHAR(100) NOT NULL,
    GiaCoBan    DECIMAL(12,0) NOT NULL,
    MucKhanCap  TINYINT       NOT NULL CONSTRAINT CK_LoaiSuCo_Khan CHECK (MucKhanCap BETWEEN 1 AND 3)
);

/* ---------- 3. Tho ---------- */
CREATE TABLE Tho (
    Id               INT NOT NULL CONSTRAINT PK_Tho PRIMARY KEY
                     CONSTRAINT FK_Tho_NguoiDung FOREIGN KEY REFERENCES NguoiDung(Id),
    TrangThaiOnline  BIT           NOT NULL CONSTRAINT DF_Tho_Online DEFAULT 0,
    IsBusy           BIT           NOT NULL CONSTRAINT DF_Tho_Busy DEFAULT 0,
    DiemUyTin        INT           NOT NULL CONSTRAINT DF_Tho_Uytin DEFAULT 100,
    DiemTrungBinh    DECIMAL(3,2)  NOT NULL CONSTRAINT DF_Tho_Diem DEFAULT 5.00,
    TiLeNhanCuoc     DECIMAL(4,3)  NOT NULL CONSTRAINT DF_Tho_TiLe DEFAULT 1.000,
    DaDuyet          BIT           NOT NULL CONSTRAINT DF_Tho_Duyet DEFAULT 0
);

/* Tho lam duoc nhung loai su co nao (nhieu-nhieu) */
CREATE TABLE ThoLoaiSuCo (
    ThoId       INT NOT NULL CONSTRAINT FK_TLSC_Tho REFERENCES Tho(Id),
    LoaiSuCoId  INT NOT NULL CONSTRAINT FK_TLSC_LoaiSuCo REFERENCES LoaiSuCo(Id),
    CONSTRAINT PK_ThoLoaiSuCo PRIMARY KEY (ThoId, LoaiSuCoId)
);

/* ---------- 4. ViTriTho (vi tri moi nhat, 1 dong moi tho) ---------- */
CREATE TABLE ViTriTho (
    ThoId       INT NOT NULL CONSTRAINT PK_ViTriTho PRIMARY KEY
                CONSTRAINT FK_ViTriTho_Tho FOREIGN KEY REFERENCES Tho(Id),
    Latitude    DECIMAL(9,6) NOT NULL,
    Longitude   DECIMAL(9,6) NOT NULL,
    CapNhatLuc  DATETIME2    NOT NULL CONSTRAINT DF_ViTriTho_CapNhat DEFAULT SYSUTCDATETIME()
);
CREATE INDEX IX_ViTriTho_ToaDo ON ViTriTho (Latitude, Longitude);

/* ---------- 5. CuocCuuHo ---------- */
CREATE TABLE CuocCuuHo (
    Id           BIGINT IDENTITY(1,1) CONSTRAINT PK_CuocCuuHo PRIMARY KEY,
    KhachId      INT NOT NULL CONSTRAINT FK_Cuoc_Khach REFERENCES NguoiDung(Id),
    ThoId        INT NULL     CONSTRAINT FK_Cuoc_Tho REFERENCES Tho(Id),
    LoaiSuCoId   INT NOT NULL CONSTRAINT FK_Cuoc_LoaiSuCo REFERENCES LoaiSuCo(Id),
    Latitude     DECIMAL(9,6) NOT NULL,
    Longitude    DECIMAL(9,6) NOT NULL,
    DiaChi       NVARCHAR(255) NULL,
    GhiChu       NVARCHAR(500) NULL,
    TrangThai    VARCHAR(20)  NOT NULL CONSTRAINT DF_Cuoc_TT DEFAULT 'DANG_TIM'
                 CONSTRAINT CK_Cuoc_TT CHECK (TrangThai IN
                   ('DANG_TIM','DA_NHAN','DANG_DEN','DA_DEN','DANG_SUA','CHO_THANH_TOAN','HOAN_THANH','HUY','KHONG_CO_THO')),
    GiaUocTinh   DECIMAL(12,0) NOT NULL,
    TongTien     DECIMAL(12,0) NULL,
    LyDoHuy      NVARCHAR(255) NULL,
    NgayTao      DATETIME2    NOT NULL CONSTRAINT DF_Cuoc_NgayTao DEFAULT SYSUTCDATETIME(),
    /* Moc bat dau dem thoi gian tim tho; dat lai khi tho tra cuoc (xem QuyTacPhatCuoc) */
    BatDauTimLuc DATETIME2    NOT NULL CONSTRAINT DF_Cuoc_BatDauTim DEFAULT SYSUTCDATETIME(),
    [RowVersion] ROWVERSION
);
CREATE INDEX IX_Cuoc_TrangThai ON CuocCuuHo (TrangThai);
CREATE INDEX IX_Cuoc_Tho ON CuocCuuHo (ThoId, TrangThai);
CREATE INDEX IX_Cuoc_Khach ON CuocCuuHo (KhachId, NgayTao DESC);

/* ---------- 6. LichSuTrangThai ---------- */
CREATE TABLE LichSuTrangThai (
    Id               BIGINT IDENTITY(1,1) CONSTRAINT PK_LichSu PRIMARY KEY,
    CuocId           BIGINT NOT NULL CONSTRAINT FK_LichSu_Cuoc REFERENCES CuocCuuHo(Id),
    TuTrangThai      VARCHAR(20) NULL,
    DenTrangThai     VARCHAR(20) NOT NULL,
    ThoiGian         DATETIME2   NOT NULL CONSTRAINT DF_LichSu_TG DEFAULT SYSUTCDATETIME(),
    NguoiThucHienId  INT NULL CONSTRAINT FK_LichSu_NguoiDung REFERENCES NguoiDung(Id),
    GhiChu           NVARCHAR(255) NULL
);
CREATE INDEX IX_LichSu_Cuoc ON LichSuTrangThai (CuocId, ThoiGian);

/* ---------- 7. VatTuPhatSinh ---------- */
CREATE TABLE VatTuPhatSinh (
    Id              BIGINT IDENTITY(1,1) CONSTRAINT PK_VatTu PRIMARY KEY,
    CuocId          BIGINT NOT NULL CONSTRAINT FK_VatTu_Cuoc REFERENCES CuocCuuHo(Id),
    TenVatTu        NVARCHAR(100) NOT NULL,
    DonGia          DECIMAL(12,0) NOT NULL,
    SoLuong         INT NOT NULL CONSTRAINT DF_VatTu_SL DEFAULT 1 CONSTRAINT CK_VatTu_SL CHECK (SoLuong > 0),
    KhachDaXacNhan  BIT NOT NULL CONSTRAINT DF_VatTu_XN DEFAULT 0
);

/* ---------- 8. DanhGia ---------- */
CREATE TABLE DanhGia (
    Id       BIGINT IDENTITY(1,1) CONSTRAINT PK_DanhGia PRIMARY KEY,
    CuocId   BIGINT NOT NULL CONSTRAINT UQ_DanhGia_Cuoc UNIQUE
             CONSTRAINT FK_DanhGia_Cuoc REFERENCES CuocCuuHo(Id),
    SoSao    TINYINT NOT NULL CONSTRAINT CK_DanhGia_Sao CHECK (SoSao BETWEEN 1 AND 5),
    NhanXet  NVARCHAR(500) NULL,
    NgayTao  DATETIME2 NOT NULL CONSTRAINT DF_DanhGia_NgayTao DEFAULT SYSUTCDATETIME()
);
GO

/* ============================================================
   DU LIEU MAU
   (MatKhauHash chi la cho giu cho; tai khoan that se tao qua API co BCrypt)
   Toa do mau ben duoi la diem tuy y - hay doi cho phu hop khu vuc demo cua nhom.
   ============================================================ */
INSERT INTO LoaiSuCo (TenSuCo, GiaCoBan, MucKhanCap) VALUES
 (N'Thủng lốp / vá xăm', 80000, 2),
 (N'Hết xăng', 60000, 2),
 (N'Chết máy, không nổ', 150000, 2),
 (N'Hỏng ắc quy', 120000, 2),
 (N'Tai nạn nhẹ, cần kéo xe', 300000, 3);

/* Id duoc sinh tu 1 vi bang vua tao moi: 1=admin, 2-4=khach, 5-9=tho */
INSERT INTO NguoiDung (HoTen, SoDienThoai, MatKhauHash, VaiTro) VALUES
 (N'Quản trị viên',   '0900000001', 'CHO_BCRYPT', 'ADMIN'),
 (N'Khách An',        '0900000002', 'CHO_BCRYPT', 'USER'),
 (N'Khách Bình',      '0900000003', 'CHO_BCRYPT', 'USER'),
 (N'Khách Chi',       '0900000004', 'CHO_BCRYPT', 'USER'),
 (N'Thợ Dũng',        '0911000005', 'CHO_BCRYPT', 'THO'),
 (N'Thợ Em',          '0911000006', 'CHO_BCRYPT', 'THO'),
 (N'Thợ Phúc',        '0911000007', 'CHO_BCRYPT', 'THO'),
 (N'Thợ Giang',       '0911000008', 'CHO_BCRYPT', 'THO'),
 (N'Thợ Hải',         '0911000009', 'CHO_BCRYPT', 'THO');

INSERT INTO Tho (Id, TrangThaiOnline, IsBusy, DiemUyTin, DiemTrungBinh, TiLeNhanCuoc, DaDuyet) VALUES
 (5, 1, 0, 100, 4.80, 0.900, 1),   -- online, ranh
 (6, 1, 0,  95, 4.50, 0.800, 1),   -- online, ranh
 (7, 1, 1, 100, 4.90, 0.950, 1),   -- online nhung dang ban
 (8, 1, 0,  90, 4.20, 0.700, 1),   -- online, ranh, o xa
 (9, 0, 0, 100, 4.70, 0.850, 1);   -- offline

INSERT INTO ThoLoaiSuCo (ThoId, LoaiSuCoId) VALUES
 (5,1),(5,2),(5,3),
 (6,1),(6,2),
 (7,1),(7,2),(7,3),(7,4),(7,5),
 (8,1),(8,4),
 (9,1),(9,2);

INSERT INTO ViTriTho (ThoId, Latitude, Longitude) VALUES
 (5, 10.780000, 106.704000),
 (6, 10.770000, 106.695000),
 (7, 10.790000, 106.720000),
 (8, 10.830000, 106.760000),
 (9, 10.775000, 106.705000);
GO

SELECT 'Tao CSDL thanh cong' AS KetQua,
       (SELECT COUNT(*) FROM NguoiDung) AS SoNguoiDung,
       (SELECT COUNT(*) FROM Tho)       AS SoTho,
       (SELECT COUNT(*) FROM LoaiSuCo)  AS SoLoaiSuCo;
GO
