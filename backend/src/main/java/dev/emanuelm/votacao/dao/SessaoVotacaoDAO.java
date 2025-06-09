package dev.emanuelm.votacao.dao;

import dev.emanuelm.votacao.domain.Pauta;
import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.dto.SessaoRequestDTO;
import dev.emanuelm.votacao.exceptions.GenericServiceError;
import dev.emanuelm.votacao.exceptions.SessaoPersistenceError;
import dev.emanuelm.votacao.repository.PautaRepository;
import dev.emanuelm.votacao.repository.SessaoVotacaoRepository;
import dev.emanuelm.votacao.repository.VotoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SessaoVotacaoDAO {

  private final SessaoVotacaoRepository sessaoVotacaoRepository;
  private final PautaRepository pautaRepository;
  private final VotoRepository votoRepository;

  public SessaoVotacaoDAO(SessaoVotacaoRepository sessaoVotacaoRepository, PautaRepository pautaRepository,
      VotoRepository votoRepository) {
    this.sessaoVotacaoRepository = sessaoVotacaoRepository;
    this.pautaRepository = pautaRepository;
    this.votoRepository = votoRepository;
  }

  public List<SessaoVotacao> obterSessoesPauta(Pauta pauta){
    if(pauta == null || pauta.getUuid() == null){
      throw new GenericServiceError("A pauta informada não pode ser nula.");
    }

    return sessaoVotacaoRepository.findAllByPauta(pauta);
  }

  public SessaoVotacao criarSessao(SessaoRequestDTO request) {
    if(request == null || request.pautaUuid() == null){
      throw new GenericServiceError("As informações para criar a sessão não podem ser nulas!");
    }

    Optional<Pauta> pauta = pautaRepository.findByUuid(request.pautaUuid());
    if(pauta.isEmpty()){
      throw new GenericServiceError("A Pauta informada não existe!");
    }

    try {

      LocalDateTime dataAbertura = LocalDateTime.now();

      long minutes = request.duracaoEmMinutos() != null ? request.duracaoEmMinutos() : 1;
      SessaoVotacao sessaoVotacao = new SessaoVotacao();
      sessaoVotacao.setPauta(pauta.get());
      sessaoVotacao.setDataAbertura(dataAbertura);
      sessaoVotacao.setDataFechamento(dataAbertura.plusMinutes(minutes));

      return sessaoVotacaoRepository.save(sessaoVotacao);
    }catch (Exception e){
      throw new SessaoPersistenceError("Erro ao realizar a persistência da sessão.");
    }
  }

  public SessaoVotacao obterSessao(String uuid){
    Optional<SessaoVotacao> sessaoVotacao = sessaoVotacaoRepository.findByUuid(uuid);
    if(sessaoVotacao.isEmpty()){
      throw new GenericServiceError("A sessão informada não foi encontrada!");
    }
    return sessaoVotacao.get();
  }

  public void deletarSessao(String uuid) {
    SessaoVotacao sessaoVotacao =  obterSessao(uuid);
    try{
      sessaoVotacaoRepository.delete(sessaoVotacao);
    }catch (DataIntegrityViolationException e){
      throw new SessaoPersistenceError("Não é possível deletar essa sessão pois existem votos registrados nela!");
    }
  }

}
