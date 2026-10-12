package com.sosrescuenow.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

@Entity
@Table(name = "VatTuPhatSinh")
public class VatTuPhatSinhEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "CuocId", nullable = false)
    private Long cuocId;

    @Nationalized
    @Column(name = "TenVatTu", nullable = false, length = 100)
    private String tenVatTu;

    @Column(name = "DonGia", nullable = false, precision = 12, scale = 0)
    private BigDecimal donGia;

    @Column(name = "SoLuong", nullable = false)
    private int soLuong = 1;

    @Column(name = "KhachDaXacNhan", nullable = false)
    private boolean khachDaXacNhan = false;

    /** Bat buoc cho JPA/Hibernate. */
    public VatTuPhatSinhEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCuocId() {
        return cuocId;
    }

    public void setCuocId(Long cuocId) {
        this.cuocId = cuocId;
    }

    public String getTenVatTu() {
        return tenVatTu;
    }

    public void setTenVatTu(String tenVatTu) {
        this.tenVatTu = tenVatTu;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public boolean isKhachDaXacNhan() {
        return khachDaXacNhan;
    }

    public void setKhachDaXacNhan(boolean khachDaXacNhan) {
        this.khachDaXacNhan = khachDaXacNhan;
    }
}
