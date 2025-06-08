package dev.emanuelm.votacao.dto;

import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.utils.DataUtils;

public record SessaoResponseDTO (String uuid, String pautaUuid, String dataAbertura, String dataFechamento) {

  public SessaoResponseDTO(SessaoVotacao sessaoVotacao) {
    this(
        sessaoVotacao.getUuid(),
        sessaoVotacao.getPauta().getUuid(),
        DataUtils.fromLocalDateTime(sessaoVotacao.getDataAbertura()),
        DataUtils.fromLocalDateTime(sessaoVotacao.getDataFechamento())
    );
  }
}