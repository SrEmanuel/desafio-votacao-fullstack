package dev.emanuelm.votacao.services;

import dev.emanuelm.votacao.enuns.CPFVotacaoStatus;
import dev.emanuelm.votacao.exceptions.CPFValidatorException;
import java.util.Objects;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class CPFValidatorService {
  private static final Random RANDOM = new Random();

  public CPFVotacaoStatus checkVotingStatus(String cpf) {
    if(Objects.equals(cpf, "12345678910")) {
      return CPFVotacaoStatus.ABLE_TO_VOTE;
    }

    if(Objects.equals(cpf, "00000000000")) {
      return CPFVotacaoStatus.UNABLE_TO_VOTE;
    }

    if (RANDOM.nextBoolean()) {
      return RANDOM.nextBoolean() ? CPFVotacaoStatus.ABLE_TO_VOTE : CPFVotacaoStatus.UNABLE_TO_VOTE;
    } else {
      throw new CPFValidatorException("CPF não encontrado!");
    }
  }
}
