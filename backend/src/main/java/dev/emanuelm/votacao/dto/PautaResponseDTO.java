package dev.emanuelm.votacao.dto;

import dev.emanuelm.votacao.domain.Pauta;

public record PautaResponseDTO (String uuid, String titulo, String descricao){

  public PautaResponseDTO(Pauta pauta) {
    this(pauta.getUuid(), pauta.getTitulo(), pauta.getDescricao());
  }
}

