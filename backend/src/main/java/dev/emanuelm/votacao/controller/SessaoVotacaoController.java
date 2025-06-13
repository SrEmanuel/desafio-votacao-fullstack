package dev.emanuelm.votacao.controller;

import dev.emanuelm.votacao.dao.PautaDAO;
import dev.emanuelm.votacao.dao.SessaoVotacaoDAO;
import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.dto.SessaoRequestDTO;
import dev.emanuelm.votacao.dto.SessaoResponseDTO;
import dev.emanuelm.votacao.dto.SessaoResultadoDTO;
import dev.emanuelm.votacao.dto.VotoRequestDTO;
import dev.emanuelm.votacao.dao.VotoDAO;
import dev.emanuelm.votacao.services.VotacaoResultadoService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/sessoes")
public class SessaoVotacaoController {

  private final SessaoVotacaoDAO sessaoVotacaoDAO;
  private final PautaDAO pautaDAO;
  private final VotoDAO votacaoService;
  private final VotacaoResultadoService votacaoResultadoService;

  public SessaoVotacaoController(SessaoVotacaoDAO sessaoVotacaoDAO, PautaDAO pautaDAO,
      VotoDAO votacaoService, VotacaoResultadoService votacaoResultadoService) {
    this.sessaoVotacaoDAO = sessaoVotacaoDAO;
    this.pautaDAO = pautaDAO;
    this.votacaoService = votacaoService;
    this.votacaoResultadoService = votacaoResultadoService;
  }

  @PostMapping
  public ResponseEntity<SessaoResponseDTO> criarSessao(@RequestBody SessaoRequestDTO request){
    SessaoVotacao sessaoVotacao =  sessaoVotacaoDAO.criarSessao(request);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{uuid}")
        .buildAndExpand(sessaoVotacao.getUuid())
        .toUri();

    return ResponseEntity.created(location).body(new SessaoResponseDTO(sessaoVotacao));
  }

  @DeleteMapping("/{uuid}")
  public ResponseEntity<Void> deletarSessao(@PathVariable String uuid){
    sessaoVotacaoDAO.deletarSessao(uuid);
    return ResponseEntity.ok().build();
  }


  @PostMapping("/{sessaoUuid}/votos")
  public ResponseEntity<Void> votarNaSessao(@PathVariable String sessaoUuid, @RequestBody
      VotoRequestDTO votoRequestDTO){
    votacaoService.votar(sessaoUuid, votoRequestDTO);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{sessaoUuid}/resultado")
  public ResponseEntity<SessaoResultadoDTO> obterResultado(@PathVariable String sessaoUuid){
    return ResponseEntity.ok().body(votacaoResultadoService.obterResultado(sessaoUuid));
  }

  @GetMapping("/{sessaoUuid}")
  public ResponseEntity<SessaoResponseDTO> obterSessao(@PathVariable String sessaoUuid){
    return ResponseEntity.ok().body(new SessaoResponseDTO(sessaoVotacaoDAO.obterSessao(sessaoUuid)));
  }








}
