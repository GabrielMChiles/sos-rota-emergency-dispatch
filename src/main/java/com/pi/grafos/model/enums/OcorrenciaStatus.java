package com.pi.grafos.model.enums;

public enum OcorrenciaStatus {
    ABERTA("Aberta"),
    DESPACHADA("Despachada"),
    EM_ATENDIMENTO("Em Atendimento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    OcorrenciaStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}