package com.pi.grafos.model.enums;

public enum OcorrenciaGravidade {
    ALTA("Alta"), MEDIA("Média"), BAIXA("Baixa");

    private final String descricao;

    OcorrenciaGravidade(String descricao) {
        this.descricao = descricao;
    }
}