package com.sosrescuenow.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Vi tri moi nhat cua tho (mot dong moi tho). */
@Entity
@Table(name = "ViTriTho")
public class ViTriThoEntity {

    @Id
    @Column(name = "ThoId")
    private Integer thoId;

    @Column(name = "Latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "Longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "CapNhatLuc", nullable = false)
    private LocalDateTime capNhatLuc;

    /** Bat buoc cho JPA/Hibernate. */
    public ViTriThoEntity() {
    }

    public Integer getThoId() {
        return thoId;
    }

    public void setThoId(Integer thoId) {
        this.thoId = thoId;
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

    public LocalDateTime getCapNhatLuc() {
        return capNhatLuc;
    }

    public void setCapNhatLuc(LocalDateTime capNhatLuc) {
        this.capNhatLuc = capNhatLuc;
    }
}
