package dev.emanuelm.votacao.dao;

import dev.emanuelm.votacao.domain.Pauta;
import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.dto.PautaRequestDTO;
import dev.emanuelm.votacao.dto.PautaResponseDTO;
import dev.emanuelm.votacao.exceptions.GenericServiceError;
import dev.emanuelm.votacao.exceptions.PautaPersistenceError;
import dev.emanuelm.votacao.repository.PautaRepository;
import dev.emanuelm.votacao.repository.SessaoVotacaoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Classe responsável pelo controle do estado de Pautas no banco de dados
 */
@Service
public class PautaDAO {

  private final PautaRepository pautaRepository;
  private final SessaoVotacaoDAO sessaoVotacaoRepository;

  public PautaDAO(PautaRepository pautaRepository, SessaoVotacaoDAO sessaoVotacaoRepository) {
    this.pautaRepository = pautaRepository;
    this.sessaoVotacaoRepository = sessaoVotacaoRepository;
  }

  /**
   * Realiza a persistência de uma pauta no sistema.
   * @param pautaRequestDTO Dados para a criaçaõ de uma pauta.
   * @return Dados da pauta após salvamento
   */
  public Pauta salvarPauta(PautaRequestDTO pautaRequestDTO){
    if(pautaRequestDTO == null){
      throw new GenericServiceError("É necessário o envio dos dados da pauta!");
    }

    if(pautaRequestDTO.titulo() == null){
      throw new GenericServiceError("O título da pauta é obrigatório.");
    }

    if(pautaRequestDTO.descricao() == null){
      //Decidi por manter a descriçaõ da pauta obrigatória, pois, geralmente essas sessões de
      //votação são sobre assuntos importantes e, assim, é legal sempre informar os colaboradoes
      //do que estão votando.
      throw new GenericServiceError("A descrição da pauta é obrigatória");
    }

    try{
      Pauta pauta = new Pauta(pautaRequestDTO.titulo(), pautaRequestDTO.descricao());
      return pautaRepository.save(pauta);

      //Pensei em implementar um salvamento de arquivos para a pauta. Contudo, não estava informado
      //inicialmente no escopo do projeto e, dessa forma, optei por deixar. Entretanto, eu acho
      //que, se fosse um sistema em produção, valeria muito a pena!

      /*if(multipartFile != null && !multipartFile.isEmpty()){
        arquivoDAO.salvarArquivoPauta(pauta, multipartFile.getInputStream());
      }*/

    }catch (Exception e){
      throw new PautaPersistenceError("Houve um erro ao salvar a pauta. Tente novamente ou contacte o administrador do sistema");
    }
  }


  public Pauta obterPauta(String uuid){
    if(uuid == null){
      throw new GenericServiceError("O UUID da pauta não pode ser nulo!");
    }

    Optional<Pauta> pauta = pautaRepository.findByUuid(uuid);
    if(pauta.isEmpty()){
      throw new GenericServiceError("Não existe nenhuma pauta com esse UUID.");
    }

    return pauta.get();
  }

  public void deletarPauta(String uuid) {
    if(uuid == null){
      throw new GenericServiceError("O UUID da pauta deve ser informado!");
    }

    try {
      Pauta pauta = obterPauta(uuid);
      pautaRepository.delete(pauta);
    } catch (DataIntegrityViolationException e){
      throw new PautaPersistenceError("Não é possível realizar modificações nessa pauta pois já existem sessões atreladas a ela.");
    }
  }

  public List<Pauta> obterPautas() {
    return pautaRepository.findAll();
  }

  public List<SessaoVotacao> obterSessoes(String pautaUuid) {
    Pauta pauta = obterPauta(pautaUuid);
    return sessaoVotacaoRepository.obterSessoesPauta(pauta);
  }
}
