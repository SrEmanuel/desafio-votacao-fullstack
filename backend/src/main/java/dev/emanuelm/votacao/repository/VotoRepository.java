package dev.emanuelm.votacao.repository;

import dev.emanuelm.votacao.domain.SessaoVotacao;
import dev.emanuelm.votacao.domain.Voto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {

  int countBySessaoVotacao(SessaoVotacao sessaoVotacao);

  List<Voto> findAllBySessaoVotacao(SessaoVotacao sessaoVotacao);

}
