package com.sojus.domain.entity;

import com.sojus.domain.enums.AssetStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "hardware")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "juzgado")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Hardware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String inventarioPatrimonial;

    @Column(length = 50)
    private String numeroSerie;

    @Column(nullable = false, length = 50)
    private String clase;

    @Column(length = 50)
    private String tipo;

    @Column(length = 80)
    private String marca;

    @Column(length = 100)
    private String modelo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AssetStatus estado = AssetStatus.ACTIVO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "juzgado_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Juzgado juzgado;

    @Column(length = 100)
    private String ubicacionFisica;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
