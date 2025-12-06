package com.pi.grafos.model;

import com.pi.grafos.model.enums.OcorrenciaGravidade;
import com.pi.grafos.model.enums.OcorrenciaStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ocorrencias")
@Data
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOcorrencia;

    private String descricao; // Campo observações

    @ManyToOne
    @JoinColumn(name = "id_local")
    private Localizacao local;

    @ManyToOne
    @JoinColumn(name = "id_tipo_ocorrencia")
    private TipoOcorrencia tipoOcorrencia; // Ex: Acidente, Mal Súbito

    @Enumerated(EnumType.STRING)
    private OcorrenciaGravidade gravidade;

    @Enumerated(EnumType.STRING)
    private OcorrenciaStatus status;

    private LocalDateTime dataAbertura;
}