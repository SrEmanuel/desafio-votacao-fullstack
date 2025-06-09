package dev.emanuelm.votacao.dto;

public record ResultadoDTO(Integer total, Integer votosSim, Integer votosNao, Boolean aprovado) {
}
