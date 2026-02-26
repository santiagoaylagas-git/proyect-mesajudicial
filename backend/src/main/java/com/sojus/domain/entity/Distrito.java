package com.sojus.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "distritos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "circunscripcion", "edificios" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Distrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String ciudad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circunscripcion_id", nullable = false)
    @JsonBackReference("circunscripcion-distritos")
    private Circunscripcion circunscripcion;

    @OneToMany(mappedBy = "distrito", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonManagedReference("distrito-edificios")
    private List<Edificio> edificios = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
