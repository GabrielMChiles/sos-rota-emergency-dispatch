package com.pi.grafos.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "atendimentos")
@Data
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAtendimento;

    @ManyToOne
    @JoinColumn(name = "id_ocorrencia", nullable = false)
    private Ocorrencia ocorrencia;

    @ManyToOne
    @JoinColumn(name = "id_ambulancia", nullable = false)
    private Ambulancia ambulancia;

    private LocalDateTime dataHoraDespacho;

    // Será preenchido quando a ambulância voltar para a base
    private LocalDateTime dataHoraConclusao;

    // Dados históricos para relatórios (congelados no momento do despacho)
    private Double distanciaPercorridaKm;
}