package dev.emanuelm.votacao.services;

import dev.emanuelm.votacao.dao.SessaoVotacaoDAO;
import dev.emanuelm.votacao.dao.VotoDAO;
import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.domain.Voto;
import dev.emanuelm.votacao.dto.PautaResponseDTO;
import dev.emanuelm.votacao.dto.ResultadoDTO;
import dev.emanuelm.votacao.dto.SessaoResponseDTO;
import dev.emanuelm.votacao.dto.SessaoResultadoDTO;
import dev.emanuelm.votacao.exceptions.GenericServiceError;
import dev.emanuelm.votacao.exceptions.ResultadoError;
import dev.emanuelm.votacao.repository.VotoRepository;
import dev.emanuelm.votacao.utils.DataUtils;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VotacaoResultadoService {

  private final SessaoVotacaoDAO sessaoVotacaoDAO;
  private final VotoRepository votoRepository;


  public VotacaoResultadoService(SessaoVotacaoDAO sessaoVotacaoDAO, VotoRepository votoRepository) {
    this.sessaoVotacaoDAO = sessaoVotacaoDAO;
    this.votoRepository = votoRepository;
  }

  public SessaoResultadoDTO obterResultado(String sessaoUuid){
    if (sessaoUuid == null) {
      throw new GenericServiceError("O UUID da sessão de votação não pode ser nulo.");
    }

    SessaoVotacao sessaoVotacao = this.sessaoVotacaoDAO.obterSessao(sessaoUuid);

    if(sessaoVotacao.getDataFechamento().isAfter(LocalDateTime.now())){
      throw new ResultadoError("Não foi possível obter os resultados dessa sessão pois a sessão ainda está aberta.");
    }

    try{

      ResultadoDTO resultadoDTO;
      List<Voto> votos = votoRepository.findAllBySessaoVotacao(sessaoVotacao);

      int total = votos.size();
      long votosSim = votos.stream().filter(Voto::getVotoSim).count();
      long votosNao = votos.stream().filter(voto -> voto.getVotoSim() == false).count();
      resultadoDTO = new ResultadoDTO(total, (int) votosSim, (int) votosNao, votosSim > votosNao);

      return new SessaoResultadoDTO(sessaoVotacao.getUuid(),
          DataUtils.fromLocalDateTime(sessaoVotacao.getDataAbertura()),
          DataUtils.fromLocalDateTime(sessaoVotacao.getDataFechamento()),
          new PautaResponseDTO(sessaoVotacao.getPauta()),
          resultadoDTO);
    }catch (Exception e){
      throw new ResultadoError("Não foi possível obter o resultado dessa sessão.");
    }

  }

}
