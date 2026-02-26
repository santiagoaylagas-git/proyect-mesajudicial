package com.sojus.domain.entity;

import com.sojus.domain.enums.Priority;
import com.sojus.domain.enums.TicketStatus;
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
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "juzgado", "solicitante", "tecnicoAsignado", "hardwareAfectado" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TicketStatus status = TicketStatus.SOLICITADO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private Priority prioridad = Priority.MEDIA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "juzgado_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    private Juzgado juzgado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "password", "juzgado" })
    private User solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_asignado_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "password", "juzgado" })
    private User tecnicoAsignado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hardware_id")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "juzgado" })
    private Hardware hardwareAfectado;

    @Column(columnDefinition = "TEXT")
    private String bitacora;

    @Column(length = 50)
    private String canal;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == TicketStatus.CERRADO && this.closedAt == null) {
            this.closedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.status == TicketStatus.CERRADO && this.closedAt == null) {
            this.closedAt = LocalDateTime.now();
        }
    }
}
