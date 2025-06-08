package dev.emanuelm.votacao.controller;

import dev.emanuelm.votacao.dao.PautaDAO;
import dev.emanuelm.votacao.domain.Pauta;
import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.dto.PautaRequestDTO;
import dev.emanuelm.votacao.dto.PautaResponseDTO;
import dev.emanuelm.votacao.dto.SessaoResponseDTO;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/v1/pautas")
public class PautaController {

  private final PautaDAO pautaDAO;

  public PautaController(PautaDAO pautaDAO) {
    this.pautaDAO = pautaDAO;
  }

  @PostMapping
  public ResponseEntity<PautaResponseDTO> salvarPauta(@RequestBody PautaRequestDTO pautaRequestDTO){
    Pauta pauta = pautaDAO.salvarPauta(pautaRequestDTO);

    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{uuid}")
        .buildAndExpand(pauta.getUuid())
        .toUri();

    return ResponseEntity.created(location).body(new PautaResponseDTO(pauta.getUuid(), pauta.getTitulo(), pauta.getDescricao()));
  }


  @DeleteMapping("/{uuid}")
  public ResponseEntity<Void> deletarPauta(@PathVariable("uuid") String uuid){
    pautaDAO.deletarPauta(uuid);
    return ResponseEntity.ok().build();
  }

  @GetMapping()
  public ResponseEntity<List<PautaResponseDTO>> obterPautas(){
    List<Pauta> pautasList = pautaDAO.obterPautas();
    return ResponseEntity.ok().body(pautasList.stream().map(pauta ->
        new PautaResponseDTO(pauta.getUuid(), pauta.getTitulo(), pauta.getDescricao())).toList());
  }


  @GetMapping("/{pautaUuid}/sessoes")
  public ResponseEntity<List<SessaoResponseDTO>> obterSessoesPauta(@PathVariable String pautaUuid){
    List<SessaoVotacao> sessoes = pautaDAO.obterSessoes(pautaUuid);
    return ResponseEntity.ok().body(sessoes.stream().map(SessaoResponseDTO::new).toList());
  }




}
