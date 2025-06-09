package dev.emanuelm.votacao.dao;

import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.domain.Voto;
import dev.emanuelm.votacao.dto.VotoRequestDTO;
import dev.emanuelm.votacao.enuns.CPFVotacaoStatus;
import dev.emanuelm.votacao.exceptions.GenericServiceError;
import dev.emanuelm.votacao.exceptions.VotoPersistenceError;
import dev.emanuelm.votacao.repository.SessaoVotacaoRepository;
import dev.emanuelm.votacao.repository.VotoRepository;
import dev.emanuelm.votacao.services.CPFValidatorService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class VotoDAO {

  private final SessaoVotacaoRepository sessaoVotacaoRepository;
  private final VotoRepository votoRepository;
  private final CPFValidatorService cpfValidatorService;

  public VotoDAO(SessaoVotacaoRepository sessaoVotacaoRepository, VotoRepository votoRepository,
      CPFValidatorService cpfValidatorService) {
    this.sessaoVotacaoRepository = sessaoVotacaoRepository;
    this.votoRepository = votoRepository;
    this.cpfValidatorService = cpfValidatorService;
  }

  /**
   * Registra o voto de um associado.
   *
   * O controle de integridade (Ou seja, se o associado votou mais de uma vez ou não) é feito
   * por meio de uma constraint no banco de dados.
   * @param sessaoUuid
   * @param votoRequestDTO
   */
  public void votar(String sessaoUuid, VotoRequestDTO votoRequestDTO){
    if(sessaoUuid == null){
      throw new GenericServiceError("O UUID da sessão de votação não pode ser nulo");
    }

    if(votoRequestDTO.cpfAssociado() == null || votoRequestDTO.cpfAssociado().isBlank()){
      throw new GenericServiceError("O CPF do assoociado não pode estar em branco.");
    }

    if(votoRequestDTO.votoSim() == null){
      throw new GenericServiceError("O voto NÃO pode estar em branco.");
    }

    Optional<SessaoVotacao> sessaoVotacao = sessaoVotacaoRepository.findByUuid(sessaoUuid);
    if(sessaoVotacao.isEmpty()){
      throw new GenericServiceError("A sessão informada não existe.");
    }

    LocalDateTime dateTime = LocalDateTime.now();
    if(dateTime.isAfter(sessaoVotacao.get().getDataFechamento())){
      throw new GenericServiceError("Não é possível votar pois a sessão já fechou.");
    }

    try{
      final CPFVotacaoStatus votacaoStatus = cpfValidatorService.checkVotingStatus(votoRequestDTO.cpfAssociado());
      if(votacaoStatus.equals(CPFVotacaoStatus.UNABLE_TO_VOTE)){
        throw new VotoPersistenceError("Não é possível votar.");
      }

      Voto voto = new Voto();
      voto.setVotoSim(votoRequestDTO.votoSim());
      voto.setSessaoVotacao(sessaoVotacao.get());
      voto.setCpfAssociado(votoRequestDTO.cpfAssociado());
      votoRepository.save(voto);

    }catch (DataIntegrityViolationException e){
      throw new VotoPersistenceError("Não é possível votar.");
    }
  }

}
