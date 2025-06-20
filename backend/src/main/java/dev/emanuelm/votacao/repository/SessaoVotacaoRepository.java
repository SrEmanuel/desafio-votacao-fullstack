package dev.emanuelm.votacao.repository;

import dev.emanuelm.votacao.domain.Pauta;
import dev.emanuelm.votacao.domain.SessaoVotacao;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

  int countByPauta(Pauta pauta);

  List<SessaoVotacao> findAllByPautaOrderByDataAberturaAsc(Pauta pauta);

  Optional<SessaoVotacao> findByUuid(String uuid);
}
