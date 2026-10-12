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
@Table(name = "LoaiSuCo")
public class LoaiSuCoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Nationalized
    @Column(name = "TenSuCo", nullable = false, length = 100)
    private String tenSuCo;

    @Column(name = "GiaCoBan", nullable = false, precision = 12, scale = 0)
    private BigDecimal giaCoBan;

    @Column(name = "MucKhanCap", nullable = false)
    private Integer mucKhanCap;

    /** Bat buoc cho JPA/Hibernate. */
    public LoaiSuCoEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenSuCo() {
        return tenSuCo;
    }

    public void setTenSuCo(String tenSuCo) {
        this.tenSuCo = tenSuCo;
    }

    public BigDecimal getGiaCoBan() {
        return giaCoBan;
    }

    public void setGiaCoBan(BigDecimal giaCoBan) {
        this.giaCoBan = giaCoBan;
    }

    public Integer getMucKhanCap() {
        return mucKhanCap;
    }

    public void setMucKhanCap(Integer mucKhanCap) {
        this.mucKhanCap = mucKhanCap;
    }
}
