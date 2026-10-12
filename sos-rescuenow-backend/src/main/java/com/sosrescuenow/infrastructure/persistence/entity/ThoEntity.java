package com.sosrescuenow.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/** Ho so tho. Id trung voi NguoiDung.Id (khong tu sinh). */
@Entity
@Table(name = "Tho")
public class ThoEntity {

    @Id
    @Column(name = "Id")
    private Integer id;

    @Column(name = "TrangThaiOnline", nullable = false)
    private boolean trangThaiOnline = false;

    /** Cot IsBusy trong CSDL: tho dang ban viec. */
    @Column(name = "IsBusy", nullable = false)
    private boolean dangBan = false;

    @Column(name = "DiemUyTin", nullable = false)
    private int diemUyTin = 100;

    @Column(name = "DiemTrungBinh", nullable = false, precision = 3, scale = 2)
    private BigDecimal diemTrungBinh = new BigDecimal("5.00");

    @Column(name = "TiLeNhanCuoc", nullable = false, precision = 4, scale = 3)
    private BigDecimal tiLeNhanCuoc = new BigDecimal("1.000");

    @Column(name = "DaDuyet", nullable = false)
    private boolean daDuyet = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ThoLoaiSuCo",
            joinColumns = @JoinColumn(name = "ThoId"),
            inverseJoinColumns = @JoinColumn(name = "LoaiSuCoId"))
    private Set<LoaiSuCoEntity> loaiSuCoLamDuoc = new HashSet<>();

    /** Bat buoc cho JPA/Hibernate. */
    public ThoEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isTrangThaiOnline() {
        return trangThaiOnline;
    }

    public void setTrangThaiOnline(boolean trangThaiOnline) {
        this.trangThaiOnline = trangThaiOnline;
    }

    public boolean isDangBan() {
        return dangBan;
    }

    public void setDangBan(boolean dangBan) {
        this.dangBan = dangBan;
    }

    public int getDiemUyTin() {
        return diemUyTin;
    }

    public void setDiemUyTin(int diemUyTin) {
        this.diemUyTin = diemUyTin;
    }

    public BigDecimal getDiemTrungBinh() {
        return diemTrungBinh;
    }

    public void setDiemTrungBinh(BigDecimal diemTrungBinh) {
        this.diemTrungBinh = diemTrungBinh;
    }

    public BigDecimal getTiLeNhanCuoc() {
        return tiLeNhanCuoc;
    }

    public void setTiLeNhanCuoc(BigDecimal tiLeNhanCuoc) {
        this.tiLeNhanCuoc = tiLeNhanCuoc;
    }

    public boolean isDaDuyet() {
        return daDuyet;
    }

    public void setDaDuyet(boolean daDuyet) {
        this.daDuyet = daDuyet;
    }

    public Set<LoaiSuCoEntity> getLoaiSuCoLamDuoc() {
        return loaiSuCoLamDuoc;
    }

    public void setLoaiSuCoLamDuoc(Set<LoaiSuCoEntity> loaiSuCoLamDuoc) {
        this.loaiSuCoLamDuoc = loaiSuCoLamDuoc;
    }
}
