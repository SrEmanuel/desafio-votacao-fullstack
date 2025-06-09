package dev.emanuelm.votacao.sessoes;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.emanuelm.votacao.domain.Pauta;
import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.domain.Voto;
import dev.emanuelm.votacao.dto.PautaResponseDTO;
import dev.emanuelm.votacao.dto.SessaoRequestDTO;
import dev.emanuelm.votacao.dto.SessaoResponseDTO;
import dev.emanuelm.votacao.dto.SessaoResultadoDTO;
import dev.emanuelm.votacao.dto.VotoRequestDTO;
import dev.emanuelm.votacao.repository.PautaRepository;
import dev.emanuelm.votacao.repository.SessaoVotacaoRepository;
import dev.emanuelm.votacao.repository.VotoRepository;
import dev.emanuelm.votacao.utils.DataUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class SessaoVotacaoControllerTest {

  private final String SESSAO_URI = "/api/v1/sessoes";

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private PautaRepository pautaRepository;

  @Autowired
  private SessaoVotacaoRepository sessaoVotacaoRepository;

  @Autowired
  private VotoRepository votoRepository;

  @Test
  void deveCriarSessaoEmPautaComSucesso(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoRequestDTO sessaoRequestDTO = new SessaoRequestDTO(pauta.getUuid(), 10);

    SessaoResponseDTO responseDTO = webTestClient.post().uri(SESSAO_URI)
        .body(BodyInserters.fromValue(sessaoRequestDTO))
        .exchange()
        .expectStatus().isCreated()
        .expectBody(SessaoResponseDTO.class)
        .returnResult().getResponseBody();


    assertNotNull(responseDTO, "A resposta não pode ser nula!");
    assertNotNull(responseDTO.dataAbertura(), "A data de abertura não pode ser nula!");
    assertNotNull(responseDTO.dataFechamento(), "A data de abertura não pode ser nula!");

    assertDoesNotThrow(() -> {
      LocalDateTime abertura = DataUtils.toLocalDateTime(responseDTO.dataAbertura());
      LocalDateTime fechamento = DataUtils.toLocalDateTime(responseDTO.dataFechamento());

      assertTrue(abertura.isBefore(fechamento), "A data de abertura tem que ser anterior a data de fechamento");
      assertTrue(abertura.plusMinutes(10).isEqual(fechamento), "A data de abertura + a 10 minutos tem que ser a data de fechamento");

    }, "A conversão das datas não pode ocasionar um erro!");
  }

  @Test
  void deveConseguirDeletarSessaoSemVoto(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setPauta(pauta);
    sessaoVotacao.setDataAbertura(LocalDateTime.now());
    sessaoVotacao.setDataFechamento(LocalDateTime.now().plusMinutes(15));
    sessaoVotacao = sessaoVotacaoRepository.save(sessaoVotacao);

    webTestClient.delete().uri(SESSAO_URI + '/' + sessaoVotacao.getUuid())
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void naoDeveConseguirDeletarSessaoSemVoto(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setPauta(pauta);
    sessaoVotacao.setDataAbertura(LocalDateTime.now());
    sessaoVotacao.setDataFechamento(LocalDateTime.now().plusMinutes(15));
    sessaoVotacao = sessaoVotacaoRepository.save(sessaoVotacao);

    Voto voto = new Voto();
    voto.setVotoSim(true);
    voto.setCpfAssociado(UUID.randomUUID().toString());
    voto.setSessaoVotacao(sessaoVotacao);
    votoRepository.save(voto);

    webTestClient.delete().uri(SESSAO_URI + '/' + sessaoVotacao.getUuid())
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void deveVotarConseguirVotarComSucesso(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setPauta(pauta);
    sessaoVotacao.setDataAbertura(LocalDateTime.now());
    sessaoVotacao.setDataFechamento(LocalDateTime.now().plusMinutes(15));
    sessaoVotacao = sessaoVotacaoRepository.save(sessaoVotacao);

    VotoRequestDTO votoRequestDTO = new VotoRequestDTO("12345678910", true);

    webTestClient.post().uri(String.format("%s/%s/votos", SESSAO_URI, sessaoVotacao.getUuid()))
        .body(BodyInserters.fromValue(votoRequestDTO))
        .exchange()
        .expectStatus().isOk();


    List<Voto> votoList = votoRepository.findAllBySessaoVotacao(sessaoVotacao);
    assertEquals(1, votoList.size(), "A quantidade de votos deve ser igual a um.");
    assertTrue(votoList.stream().allMatch(voto -> voto.getVotoSim() == votoRequestDTO.votoSim()),
        "Como só há um voto registrado, ele deveria ter sido igual ao enviado.");

  }

  @Test
  void naoDeveVotarConseguirVotarAposFechamento(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setPauta(pauta);
    sessaoVotacao.setDataAbertura(LocalDateTime.now().minusHours(1));
    sessaoVotacao.setDataFechamento(LocalDateTime.now().minusMinutes(30));
    sessaoVotacao = sessaoVotacaoRepository.save(sessaoVotacao);

    VotoRequestDTO votoRequestDTO = new VotoRequestDTO("12345678910", true);

    webTestClient.post().uri(String.format("%s/%s/votos", SESSAO_URI, sessaoVotacao.getUuid()))
        .body(BodyInserters.fromValue(votoRequestDTO))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void naoDeveVotarConseguirVotarComCPFInvalido(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setPauta(pauta);
    sessaoVotacao.setDataAbertura(LocalDateTime.now());
    sessaoVotacao.setDataFechamento(LocalDateTime.now().plusMinutes(30));
    sessaoVotacao = sessaoVotacaoRepository.save(sessaoVotacao);

    VotoRequestDTO votoRequestDTO = new VotoRequestDTO("00000000000", true);

    webTestClient.post().uri(String.format("%s/%s/votos", SESSAO_URI, sessaoVotacao.getUuid()))
        .body(BodyInserters.fromValue(votoRequestDTO))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void naoDeveVotarConseguirVotarDuasVezes(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setPauta(pauta);
    sessaoVotacao.setDataAbertura(LocalDateTime.now());
    sessaoVotacao.setDataFechamento(LocalDateTime.now().plusMinutes(30));
    sessaoVotacao = sessaoVotacaoRepository.save(sessaoVotacao);

    VotoRequestDTO votoRequestDTO = new VotoRequestDTO("12345678910", true);

    webTestClient.post().uri(String.format("%s/%s/votos", SESSAO_URI, sessaoVotacao.getUuid()))
        .body(BodyInserters.fromValue(votoRequestDTO))
        .exchange()
        .expectStatus().isOk();

    List<Voto> votoList = votoRepository.findAllBySessaoVotacao(sessaoVotacao);
    assertEquals(1, votoList.size(), "A quantidade de votos deve ser igual a um.");
    assertTrue(votoList.stream().allMatch(voto -> voto.getVotoSim() == votoRequestDTO.votoSim()),
        "Como só há um voto registrado, ele deveria ter sido igual ao enviado.");

    webTestClient.post().uri(String.format("%s/%s/votos", SESSAO_URI, sessaoVotacao.getUuid()))
        .body(BodyInserters.fromValue(votoRequestDTO))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void naoDeveObterResultadoAntesDoFechamento(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setPauta(pauta);
    sessaoVotacao.setDataAbertura(LocalDateTime.now().minusHours(1));
    sessaoVotacao.setDataFechamento(LocalDateTime.now().plusMinutes(30));
    sessaoVotacao = sessaoVotacaoRepository.save(sessaoVotacao);


    List<Voto> votos = new ArrayList<>();
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), true));
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), true));
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), true));
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), false));
    votoRepository.saveAll(votos);

    webTestClient.get().uri(String.format("%s/%s/resultado", SESSAO_URI, sessaoVotacao.getUuid()))
        .exchange()
        .expectStatus().isBadRequest();
  }


  @Test
  void deveObterResultadoAprovadoAposVotacao(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setPauta(pauta);
    sessaoVotacao.setDataAbertura(LocalDateTime.now().minusHours(1));
    sessaoVotacao.setDataFechamento(LocalDateTime.now().minusMinutes(30));
    sessaoVotacao = sessaoVotacaoRepository.save(sessaoVotacao);


    List<Voto> votos = new ArrayList<>();
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), true));
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), true));
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), true));
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), false));
    votoRepository.saveAll(votos);

    SessaoResultadoDTO sessaoResultadoDTO = webTestClient.get().uri(String.format("%s/%s/resultado", SESSAO_URI, sessaoVotacao.getUuid()))
        .exchange()
        .expectStatus().isOk()
        .expectBody(SessaoResultadoDTO.class)
        .returnResult().getResponseBody();

    assertNotNull(sessaoResultadoDTO, "O resultado não pode ser nulo!");
    assertEquals(3, sessaoResultadoDTO.resultado().votosSim(), "A contagem deve estar correta.");
    assertEquals(1, sessaoResultadoDTO.resultado().votosNao(), "A contagem deve estar correta.");
    assertTrue(sessaoResultadoDTO.resultado().aprovado(), "A sessão deveria ter sido aprovada.");
  }

  @Test
  void deveObterResultadoReprovadoAposVotacaoEmpatada(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setPauta(pauta);
    sessaoVotacao.setDataAbertura(LocalDateTime.now().minusHours(1));
    sessaoVotacao.setDataFechamento(LocalDateTime.now().minusMinutes(30));
    sessaoVotacao = sessaoVotacaoRepository.save(sessaoVotacao);


    List<Voto> votos = new ArrayList<>();
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), false));
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), true));
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), true));
    votos.add(criarVoto(sessaoVotacao, UUID.randomUUID().toString(), false));
    votoRepository.saveAll(votos);

    SessaoResultadoDTO sessaoResultadoDTO = webTestClient.get().uri(String.format("%s/%s/resultado", SESSAO_URI, sessaoVotacao.getUuid()))
        .exchange()
        .expectStatus().isOk()
        .expectBody(SessaoResultadoDTO.class)
        .returnResult().getResponseBody();

    assertNotNull(sessaoResultadoDTO, "O resultado não pode ser nulo!");
    assertEquals(2, sessaoResultadoDTO.resultado().votosSim(), "A contagem deve estar correta.");
    assertEquals(2, sessaoResultadoDTO.resultado().votosNao(), "A contagem deve estar correta.");
    assertFalse(sessaoResultadoDTO.resultado().aprovado(), "A sessão deveria ter sido reprovado.");
  }

  @Test
  void deveObterResultadoReprovadoAposVotacaoSemVotos(){
    final String nomePauta = "Pauta de Teste: " + UUID.randomUUID();
    final String descricaoPauta = "Pauta de testes para avaliar a questão da empresa.";

    Pauta pauta = new Pauta();
    pauta.setTitulo(nomePauta);
    pauta.setDescricao(descricaoPauta);
    pauta = pautaRepository.save(pauta);

    SessaoVotacao sessaoVotacao = new SessaoVotacao();
    sessaoVotacao.setPauta(pauta);
    sessaoVotacao.setDataAbertura(LocalDateTime.now().minusHours(1));
    sessaoVotacao.setDataFechamento(LocalDateTime.now().minusMinutes(30));
    sessaoVotacao = sessaoVotacaoRepository.save(sessaoVotacao);


    SessaoResultadoDTO sessaoResultadoDTO = webTestClient.get().uri(String.format("%s/%s/resultado", SESSAO_URI, sessaoVotacao.getUuid()))
        .exchange()
        .expectStatus().isOk()
        .expectBody(SessaoResultadoDTO.class)
        .returnResult().getResponseBody();

    assertNotNull(sessaoResultadoDTO, "O resultado não pode ser nulo!");
    assertEquals(0, sessaoResultadoDTO.resultado().votosSim(), "A contagem deve estar correta.");
    assertEquals(0, sessaoResultadoDTO.resultado().votosNao(), "A contagem deve estar correta.");
    assertFalse(sessaoResultadoDTO.resultado().aprovado(), "A sessão deveria ter sido reprovado.");
  }

  private Voto criarVoto(SessaoVotacao sessaoVotacao, String cpfAssociado, boolean votoSim){
    Voto voto = new Voto();
    voto.setVotoSim(votoSim);
    voto.setCpfAssociado(cpfAssociado);
    voto.setSessaoVotacao(sessaoVotacao);
    return voto;
  }




}
