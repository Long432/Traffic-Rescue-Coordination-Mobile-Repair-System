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

import java.time.LocalDateTime;

@Entity
@Table(name = "LichSuTrangThai")
public class LichSuTrangThaiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @Column(name = "CuocId", nullable = false)
    private Long cuocId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TuTrangThai", length = 20)
    private TrangThaiCuoc tuTrangThai;

    @Enumerated(EnumType.STRING)
    @Column(name = "DenTrangThai", nullable = false, length = 20)
    private TrangThaiCuoc denTrangThai;

    @Column(name = "ThoiGian", insertable = false, updatable = false)
    private LocalDateTime thoiGian;

    @Column(name = "NguoiThucHienId")
    private Integer nguoiThucHienId;

    @Nationalized
    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    /** Bat buoc cho JPA/Hibernate. */
    public LichSuTrangThaiEntity() {
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

    public TrangThaiCuoc getTuTrangThai() {
        return tuTrangThai;
    }

    public void setTuTrangThai(TrangThaiCuoc tuTrangThai) {
        this.tuTrangThai = tuTrangThai;
    }

    public TrangThaiCuoc getDenTrangThai() {
        return denTrangThai;
    }

    public void setDenTrangThai(TrangThaiCuoc denTrangThai) {
        this.denTrangThai = denTrangThai;
    }

    public LocalDateTime getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(LocalDateTime thoiGian) {
        this.thoiGian = thoiGian;
    }

    public Integer getNguoiThucHienId() {
        return nguoiThucHienId;
    }

    public void setNguoiThucHienId(Integer nguoiThucHienId) {
        this.nguoiThucHienId = nguoiThucHienId;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
