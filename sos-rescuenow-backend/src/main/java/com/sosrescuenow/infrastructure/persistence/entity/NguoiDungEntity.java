package com.sosrescuenow.infrastructure.persistence.entity;

import com.sosrescuenow.domain.model.TrangThaiTaiKhoan;
import com.sosrescuenow.domain.model.VaiTro;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(name = "NguoiDung")
public class NguoiDungEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Nationalized
    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "SoDienThoai", nullable = false, length = 15)
    private String soDienThoai;

    @Column(name = "MatKhauHash", nullable = false, length = 100)
    private String matKhauHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "VaiTro", nullable = false, length = 10)
    private VaiTro vaiTro;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThaiTaiKhoan", nullable = false, length = 10)
    private TrangThaiTaiKhoan trangThaiTaiKhoan;

    @Column(name = "KhoaDen")
    private LocalDateTime khoaDen;

    /** CSDL tu dien (DEFAULT SYSUTCDATETIME), ung dung chi doc. */
    @Column(name = "NgayTao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;

    /** Bat buoc cho JPA/Hibernate. */
    public NguoiDungEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getMatKhauHash() {
        return matKhauHash;
    }

    public void setMatKhauHash(String matKhauHash) {
        this.matKhauHash = matKhauHash;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public TrangThaiTaiKhoan getTrangThaiTaiKhoan() {
        return trangThaiTaiKhoan;
    }

    public void setTrangThaiTaiKhoan(TrangThaiTaiKhoan trangThaiTaiKhoan) {
        this.trangThaiTaiKhoan = trangThaiTaiKhoan;
    }

    public LocalDateTime getKhoaDen() {
        return khoaDen;
    }

    public void setKhoaDen(LocalDateTime khoaDen) {
        this.khoaDen = khoaDen;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }
}
