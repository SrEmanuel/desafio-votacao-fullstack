package dev.emanuelm.votacao.dao;

import dev.emanuelm.votacao.domain.Pauta;
import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.exceptions.GenericServiceError;
import dev.emanuelm.votacao.repository.SessaoVotacaoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SessaoVotacaoDAO {

  private final SessaoVotacaoRepository sessaoVotacaoRepository;


  public SessaoVotacaoDAO(SessaoVotacaoRepository sessaoVotacaoRepository) {
    this.sessaoVotacaoRepository = sessaoVotacaoRepository;
  }

  public List<SessaoVotacao> obterSessoesPauta(Pauta pauta){
    if(pauta == null || pauta.getUuid() == null){
      throw new GenericServiceError("A pauta informada não pode ser nula.");
    }

    return sessaoVotacaoRepository.findAllByPauta(pauta);
  }

}
