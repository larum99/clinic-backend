package com.dental.clinic.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "services")
public class DentalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_service")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Short durationMinutes;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    public DentalService() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Short durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
