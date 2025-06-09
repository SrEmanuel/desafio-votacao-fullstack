package dev.emanuelm.votacao.sessoes;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.emanuelm.votacao.domain.Pauta;
import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.domain.Voto;
import dev.emanuelm.votacao.dto.PautaResponseDTO;
import dev.emanuelm.votacao.dto.SessaoRequestDTO;
import dev.emanuelm.votacao.dto.SessaoResponseDTO;
import dev.emanuelm.votacao.repository.PautaRepository;
import dev.emanuelm.votacao.repository.SessaoVotacaoRepository;
import dev.emanuelm.votacao.repository.VotoRepository;
import dev.emanuelm.votacao.utils.DataUtils;
import java.time.LocalDateTime;
import java.util.UUID;
import net.bytebuddy.asm.Advice.Local;
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
}
