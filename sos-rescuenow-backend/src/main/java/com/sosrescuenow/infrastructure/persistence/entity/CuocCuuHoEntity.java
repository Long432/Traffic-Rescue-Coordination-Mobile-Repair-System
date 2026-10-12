package com.sosrescuenow.infrastructure.persistence.entity;

import com.sosrescuenow.domain.model.TrangThaiCuoc;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cuoc cuu ho. Cot RowVersion cua CSDL khong anh xa: viec chong nhan trung cuoc do cau lenh
 * UPDATE co dieu kien (WHERE TrangThai = 'DANG_TIM') dam nhiem, se them o module sau.
 */
@Entity
@Table(name = "CuocCuuHo")
public class CuocCuuHoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "KhachId", nullable = false)
    private Integer khachId;

    @Column(name = "ThoId")
    private Integer thoId;

    @Column(name = "LoaiSuCoId", nullable = false)
    private Integer loaiSuCoId;

    @Column(name = "Latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "Longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Nationalized
    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    @Nationalized
    @Column(name = "GhiChu", length = 500)
    private String ghiChu;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", nullable = false, length = 20)
    private TrangThaiCuoc trangThai = TrangThaiCuoc.DANG_TIM;

    @Column(name = "GiaUocTinh", nullable = false, precision = 12, scale = 0)
    private BigDecimal giaUocTinh;

    @Column(name = "TongTien", precision = 12, scale = 0)
    private BigDecimal tongTien;

    @Nationalized
    @Column(name = "LyDoHuy", length = 255)
    private String lyDoHuy;

    @Column(name = "NgayTao", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    /** Moc bat dau dem thoi gian tim tho (dat lai khi tho tra cuoc). */
    @Column(name = "BatDauTimLuc", nullable = false)
    private LocalDateTime batDauTimLuc;

    /** Bat buoc cho JPA/Hibernate. */
    public CuocCuuHoEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getKhachId() {
        return khachId;
    }

    public void setKhachId(Integer khachId) {
        this.khachId = khachId;
    }

    public Integer getThoId() {
        return thoId;
    }

    public void setThoId(Integer thoId) {
        this.thoId = thoId;
    }

    public Integer getLoaiSuCoId() {
        return loaiSuCoId;
    }

    public void setLoaiSuCoId(Integer loaiSuCoId) {
        this.loaiSuCoId = loaiSuCoId;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public TrangThaiCuoc getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiCuoc trangThai) {
        this.trangThai = trangThai;
    }

    public BigDecimal getGiaUocTinh() {
        return giaUocTinh;
    }

    public void setGiaUocTinh(BigDecimal giaUocTinh) {
        this.giaUocTinh = giaUocTinh;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public String getLyDoHuy() {
        return lyDoHuy;
    }

    public void setLyDoHuy(String lyDoHuy) {
        this.lyDoHuy = lyDoHuy;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getBatDauTimLuc() {
        return batDauTimLuc;
    }

    public void setBatDauTimLuc(LocalDateTime batDauTimLuc) {
        this.batDauTimLuc = batDauTimLuc;
    }
}
