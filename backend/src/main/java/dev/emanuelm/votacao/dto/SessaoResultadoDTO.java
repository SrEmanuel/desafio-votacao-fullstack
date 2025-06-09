package dev.emanuelm.votacao.dto;

public record SessaoResultadoDTO(
    String sessaoUuid,
    String dataAbertura,
    String dataFechamento,
    PautaResponseDTO pauta,
    ResultadoDTO resultado
) {

}
