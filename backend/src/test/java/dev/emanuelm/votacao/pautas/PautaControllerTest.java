package dev.emanuelm.votacao.pautas;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.emanuelm.votacao.dto.PautaRequestDTO;
import dev.emanuelm.votacao.dto.PautaResponseDTO;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PautaControllerTest {

  private final String PAUTA_URI = "/api/v1/pauta";

  @Autowired
  private WebTestClient webTestClient;


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
        .bodyValue(pautaRequestDTO)
        .exchange()
        .expectStatus().isCreated()
        .expectBody(PautaResponseDTO.class)
        .returnResult().getResponseBody();

    assertNotNull(response, "A resposta enviada não pode ser nula!");
    assertNotNull(response.uuid(), "A resposta deve conter um UUID");
    assertEquals(nomePauta, response.titulo());
    assertEquals(descricaoPauta, response.descricao());
  }








}
