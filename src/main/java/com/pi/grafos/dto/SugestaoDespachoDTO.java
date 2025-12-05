package com.pi.grafos.dto;

// Data Transfer Object (Objeto de Transferência de Dados). Comunicação entre os models/banco e a interface
public record SugestaoDespachoDTO(
        Long idAmbulancia, // Necessário para o despacho final no banco
        String placa,
        String baseOrigem,
        String tipo,       // "UTI" ou "BÁSICA"
        double distanciaKm,
        int tempoMinutos,
        boolean atendeSLA
) {}