package dev.emanuelm.votacao.pautas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.emanuelm.votacao.domain.Pauta;
import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.dto.PautaRequestDTO;
import dev.emanuelm.votacao.dto.PautaResponseDTO;
import dev.emanuelm.votacao.dto.SessaoResponseDTO;
import dev.emanuelm.votacao.repository.PautaRepository;
import dev.emanuelm.votacao.repository.SessaoVotacaoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PautaControllerTest {

  private final String PAUTA_URI = "/api/v1/pautas";

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private PautaRepository pautaRepository;

  @Autowired
  private SessaoVotacaoRepository sessaoVotacaoRepository;

  /**
   * Testa o endpoint /api/v1/pauta
   * Deve criar uma pauta com sucesso no sistema e retornar o status code 201.
   * É validado se o retorno (título e descricao) são iguais ao que foi passado e que o valor de uuid
   * retornado não seja nulo.
   */
  @Test
  void deveConseguirCriarPautaComSucesso(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";
    PautaRequestDTO pautaRequestDTO = new PautaRequestDTO(nomePauta, descricaoPauta);

    PautaResponseDTO response = webTestClient.post().uri(PAUTA_URI)
        .body(BodyInserters.fromValue(pautaRequestDTO))
        .exchange()
        .expectStatus().isCreated()
        .expectBody(PautaResponseDTO.class)
        .returnResult().getResponseBody();

    assertNotNull(response, "A resposta enviada não pode ser nula!");
    assertNotNull(response.uuid(), "A resposta deve conter um UUID");
    assertEquals(nomePauta, response.titulo());
    assertEquals(descricaoPauta, response.descricao());
  }


  @Test
  void naoDeveConseguirCriarPautaSemNomeInformarCampso(){
    PautaRequestDTO pautaRequestDTO = new PautaRequestDTO(null, null);

    webTestClient.post().uri(PAUTA_URI)
        .body(BodyInserters.fromValue(pautaRequestDTO))
        .exchange()
        .expectStatus().isBadRequest();
  }

  /**
   * Testa o endpoint /api/v1/pauta
   */
  @Test
  void deveConseguirListarPautasCadastradasNoSistema(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";
    PautaRequestDTO pautaRequestDTO = new PautaRequestDTO(nomePauta, descricaoPauta);


    PautaResponseDTO response = webTestClient.post().uri(PAUTA_URI)
        .body(BodyInserters.fromValue(pautaRequestDTO))
        .exchange()
        .expectStatus().isCreated()
        .expectBody(PautaResponseDTO.class)
        .returnResult().getResponseBody();

    assertNotNull(response, "A pauta deveria ter sido criada.");

    List<PautaResponseDTO> pautas = webTestClient.get().uri(PAUTA_URI)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(PautaResponseDTO.class)
        .returnResult().getResponseBody();

    assertNotNull(pautas, "O número de pautas retornadas não pode ser nulo.");
    assertFalse(pautas.isEmpty(), "Alguma pauta deveria ter sido retornada.");
    assertTrue(pautas.stream().anyMatch(pauta -> pauta.uuid().equals(response.uuid())), "A pauta criada deveria ter sido retornada.");
  }


  @Test
  void deveConseguirDeletarPautaSemSessoes(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    assertNotNull(pauta.getUuid(), "O UUID da pauta deveria existir, uma vez que ela foi criada.");

    webTestClient.delete().uri(PAUTA_URI + '/' + pauta.getUuid())
        .exchange()
        .expectStatus().isOk();

    assertFalse(pautaRepository.existsByUuid(pauta.getUuid()), "A pauta deveria ter sido deletada do banco de dados!");
  }

  @Test
  void naoDeveConseguirDeletarPautaComSessoes(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setDataAbertura(LocalDateTime.now());
    sessaoVotacao.setDataFechamento(LocalDateTime.now());
    sessaoVotacao.setPauta(pauta);

    sessaoVotacaoRepository.save(sessaoVotacao);

    assertNotNull(pauta.getUuid(), "O UUID da pauta deveria existir, uma vez que ela foi criada.");
    assertNotNull(sessaoVotacao.getUuid(), "O UUID da sessão deveria existir, uma vez que ela foi criada.");

    webTestClient.delete().uri(PAUTA_URI + '/' + pauta.getUuid())
        .exchange()
        .expectStatus().isBadRequest();

    assertTrue(pautaRepository.existsByUuid(pauta.getUuid()), "A pauta NÃO deveria ter sido deletada do banco de dados!");
  }


  /**
   * Testa o endpoint /api/v1/pauta
   */
  @Test
  void deveConseguirListarSessoeDePautasCadastradasNoSistema(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setDataAbertura(LocalDateTime.now());
    sessaoVotacao.setDataFechamento(LocalDateTime.now());
    sessaoVotacao.setPauta(pauta);

    sessaoVotacaoRepository.save(sessaoVotacao);

    List<SessaoResponseDTO> sessoes = webTestClient.get().uri(String.format("%s/%s/sessoes", PAUTA_URI, pauta.getUuid()))
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(SessaoResponseDTO.class)
        .returnResult().getResponseBody();

    assertNotNull(sessoes);
    assertFalse(sessoes.isEmpty());
    final String pautaUuid = pauta.getUuid();
    assertTrue(sessoes.stream().allMatch(sessao -> sessao.pautaUuid().equals(pautaUuid)), "Todos os retornos devem ser de sessoẽs da"
        + "pauta informada");
    assertEquals(1,  sessoes.size(), "Deve haver somente uma sessão.");
  }


}
